package cn.huava.common.graalvm;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.reflection.TypeParameterResolver;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.aot.BeanRegistrationExcludeFilter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.ResolvableType;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * This class and the containing class {@link NativeRuntimeHintsRegistrar} is used for building
 * GraalVM native image.<br>
 * This class's main code is to add native image support for MyBatis-Plus. <br>
 * Kudos to <a href="https://github.com/nieqiurong/mybatis-native-demo">mybatis-native-demo</a>
 *
 * @author Camio1945
 */
@Configuration(proxyBeanMethods = false)
@ImportRuntimeHints(NativeRuntimeHintsRegistrar.class)
public class RuntimeHintsRegistrarConfig {

  /**
   * This bean is necessary, otherwise this kind of error will occur:
   *
   * <pre>
   * Error creating bean with name 'sysUserRoleMapper': Unsatisfied dependency expressed through constructor parameter 0:
   * No qualifying bean of type 'java.lang.Class<?>' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {}
   * </pre>
   *
   * @return MyBatisMapperFactoryBeanPostProcessor
   */
  @Bean
  static MyBatisMapperFactoryBeanPostProcessor myBatisMapperFactoryBeanPostProcessor() {
    return new MyBatisMapperFactoryBeanPostProcessor();
  }

  /**
   * This bean is necessary, otherwise this kind of error will occor:
   *
   * <pre>
   * Exception encountered during context initialization - cancelling refresh attempt: org.springframework.context.ApplicationContextException: Unable to start web server
   * Application run failed
   * org.springframework.context.ApplicationContextException: Unable to start web server
   * ...
   * Caused by: org.springframework.boot.web.server.WebServerException: Unable to start embedded Tomcat
   * ...
   * Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'jwtAuthenticationFilter': Unsatisfied dependency expressed through constructor parameter 0: Error creating bean with name 'aceJwtService': Unsatisfied dependency expressed through constructor parameter 0: Error creating bean with name 'createTokenService': Unsatisfied dependency expressed through field 'baseMapper': Error creating bean with name 'sysUserMapper': FactoryBean threw exception on object creation
   * ...
   * Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'sysUserMapper': FactoryBean threw exception on object creation
   * </pre>
   *
   * @return MyBatisBeanFactoryInitializationAotProcessor
   */
  @Bean
  MyBatisBeanFactoryInitializationAotProcessor myBatisBeanFactoryInitializationAotProcessor() {
    return new MyBatisBeanFactoryInitializationAotProcessor();
  }

  static class MyBatisBeanFactoryInitializationAotProcessor
      implements BeanFactoryInitializationAotProcessor, BeanRegistrationExcludeFilter {

    private final Set<Class<?>> excludeClasses = new HashSet<>();

    MyBatisBeanFactoryInitializationAotProcessor() {
      excludeClasses.add(MapperScannerConfigurer.class);
    }

    @Override
    public boolean isExcludedFromAotProcessing(RegisteredBean registeredBean) {
      return excludeClasses.contains(registeredBean.getBeanClass());
    }

    @Override
    public BeanFactoryInitializationAotContribution processAheadOfTime(
        ConfigurableListableBeanFactory beanFactory) {
      String[] beanNames = beanFactory.getBeanNamesForType(MapperFactoryBean.class);
      if (beanNames.length == 0) {
        return null;
      }

      return (context, code) -> {
        RuntimeHints hints = context.getRuntimeHints();
        for (String beanName : beanNames) {
          BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName.substring(1));
          PropertyValue mapperInterface =
              beanDefinition.getPropertyValues().getPropertyValue("mapperInterface");
          if (mapperInterface != null && mapperInterface.getValue() != null) {
            Class<?> mapperInterfaceType = (Class<?>) mapperInterface.getValue();
            if (mapperInterfaceType != null) {
              registerReflectionTypeIfNecessary(mapperInterfaceType, hints);
              hints.proxies().registerJdkProxy(mapperInterfaceType);
              hints
                  .resources()
                  .registerPattern(mapperInterfaceType.getName().replace('.', '/').concat(".xml"));
              registerMapperRelationships(mapperInterfaceType, hints);
            }
          }
        }
      };
    }

    private void registerReflectionTypeIfNecessary(Class<?> type, RuntimeHints hints) {
      String java = "java";
      if (!type.isPrimitive() && !type.getName().startsWith(java)) {
        hints.reflection().registerType(type, MemberCategory.values());
      }
    }

    private void registerMapperRelationships(Class<?> mapperInterfaceType, RuntimeHints hints) {
      Method[] methods = ReflectionUtils.getAllDeclaredMethods(mapperInterfaceType);
      for (Method method : methods) {
        if (method.getDeclaringClass() != Object.class) {
          ReflectionUtils.makeAccessible(method);
          registerSqlProviderTypes(
              method, hints, SelectProvider.class, SelectProvider::value, SelectProvider::type);
          registerSqlProviderTypes(
              method, hints, InsertProvider.class, InsertProvider::value, InsertProvider::type);
          registerSqlProviderTypes(
              method, hints, UpdateProvider.class, UpdateProvider::value, UpdateProvider::type);
          registerSqlProviderTypes(
              method, hints, DeleteProvider.class, DeleteProvider::value, DeleteProvider::type);
          Class<?> returnType =
              MyBatisMapperTypeUtils.resolveReturnClass(mapperInterfaceType, method);
          registerReflectionTypeIfNecessary(returnType, hints);
          MyBatisMapperTypeUtils.resolveParameterClasses(mapperInterfaceType, method)
              .forEach(x -> registerReflectionTypeIfNecessary(x, hints));
        }
      }
    }

    @SafeVarargs
    private <T extends Annotation> void registerSqlProviderTypes(
        Method method,
        RuntimeHints hints,
        Class<T> annotationType,
        Function<T, Class<?>>... providerTypeResolvers) {
      for (T annotation : method.getAnnotationsByType(annotationType)) {
        for (Function<T, Class<?>> providerTypeResolver : providerTypeResolvers) {
          registerReflectionTypeIfNecessary(providerTypeResolver.apply(annotation), hints);
        }
      }
    }
  }

  static class MyBatisMapperTypeUtils {
    private MyBatisMapperTypeUtils() {
      // NOP
    }

    static Class<?> resolveReturnClass(Class<?> mapperInterface, Method method) {
      Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, mapperInterface);
      return typeToClass(resolvedReturnType, method.getReturnType());
    }

    static Set<Class<?>> resolveParameterClasses(Class<?> mapperInterface, Method method) {
      return Stream.of(TypeParameterResolver.resolveParamTypes(method, mapperInterface))
          .map(x -> typeToClass(x, x instanceof Class ? (Class<?>) x : Object.class))
          .collect(Collectors.toSet());
    }

    private static Class<?> typeToClass(Type src, Class<?> fallback) {
      Class<?> result = null;
      if (src instanceof Class<?>) {
        if (((Class<?>) src).isArray()) {
          result = ((Class<?>) src).getComponentType();
        } else {
          result = (Class<?>) src;
        }
      } else if (src instanceof ParameterizedType parameterizedType) {
        int index =
            (parameterizedType.getRawType() instanceof Class
                    && Map.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())
                    && parameterizedType.getActualTypeArguments().length > 1)
                ? 1
                : 0;
        Type actualType = parameterizedType.getActualTypeArguments()[index];
        result = typeToClass(actualType, fallback);
      }
      if (result == null) {
        result = fallback;
      }
      return result;
    }
  }

  static class MyBatisMapperFactoryBeanPostProcessor
      implements MergedBeanDefinitionPostProcessor, BeanFactoryAware {

    private static final org.apache.commons.logging.Log LOG =
        LogFactory.getLog(MyBatisMapperFactoryBeanPostProcessor.class);

    private static final String MAPPER_FACTORY_BEAN = "org.mybatis.spring.mapper.MapperFactoryBean";

    private ConfigurableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) {
      this.beanFactory = (ConfigurableBeanFactory) beanFactory;
    }

    @Override
    public void postProcessMergedBeanDefinition(
        @NonNull RootBeanDefinition beanDefinition,
        @NonNull Class<?> beanType,
        @NonNull String beanName) {
      if (ClassUtils.isPresent(MAPPER_FACTORY_BEAN, this.beanFactory.getBeanClassLoader())) {
        resolveMapperFactoryBeanTypeIfNecessary(beanDefinition);
      }
    }

    private void resolveMapperFactoryBeanTypeIfNecessary(RootBeanDefinition beanDefinition) {
      if (!beanDefinition.hasBeanClass()
          || !MapperFactoryBean.class.isAssignableFrom(beanDefinition.getBeanClass())) {
        return;
      }
      if (beanDefinition.getResolvableType().hasUnresolvableGenerics()) {
        Class<?> mapperInterface = getMapperInterface(beanDefinition);
        if (mapperInterface != null) {
          // Exposes a generic type information to context for prevent early initializing
          ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
          constructorArgumentValues.addGenericArgumentValue(mapperInterface);
          beanDefinition.setConstructorArgumentValues(constructorArgumentValues);
          beanDefinition.setTargetType(
              ResolvableType.forClassWithGenerics(beanDefinition.getBeanClass(), mapperInterface));
        }
      }
    }

    private Class<?> getMapperInterface(RootBeanDefinition beanDefinition) {
      try {
        return (Class<?>) beanDefinition.getPropertyValues().get("mapperInterface");
      } catch (Exception e) {
        LOG.debug("Fail getting mapper interface type.", e);
        return null;
      }
    }
  }
}
