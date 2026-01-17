package cn.huava.common.graalvm;

import static cn.huava.common.graalvm.MyBatisMapperTypeUtil.resolveParameterClasses;
import static cn.huava.common.graalvm.MyBatisMapperTypeUtil.resolveReturnClass;

import cn.huava.common.annotation.UnreachableForTesting;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.aot.BeanRegistrationExcludeFilter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.util.ReflectionUtils;

/**
 * AOT processor for MyBatis mapper factory beans.
 *
 * @author Camio1945
 */
public class MyBatisBeanFactoryInitializationAotProcessor
    implements BeanFactoryInitializationAotProcessor, BeanRegistrationExcludeFilter {

  private final Set<Class<?>> excludeClasses = new HashSet<>();

  public MyBatisBeanFactoryInitializationAotProcessor() {
    excludeClasses.add(MapperScannerConfigurer.class);
  }

  @Override
  public boolean isExcludedFromAotProcessing(RegisteredBean registeredBean) {
    return excludeClasses.contains(registeredBean.getBeanClass());
  }

  @Override
  @UnreachableForTesting
  public BeanFactoryInitializationAotContribution processAheadOfTime(
      ConfigurableListableBeanFactory beanFactory) {
    String[] beanNames = beanFactory.getBeanNamesForType(MapperFactoryBean.class);
    if (beanNames.length == 0) {
      return null;
    }

    return (context, code) -> {
      RuntimeHints hints = context.getRuntimeHints();
      for (String beanName : beanNames) {
        // Skip if bean name doesn't start with '#' (this appears to be a special prefix)
        if (!beanName.startsWith("#")) continue;

        String actualBeanName = beanName.substring(1); // Remove the '#' prefix
        try {
          BeanDefinition beanDefinition = beanFactory.getBeanDefinition(actualBeanName);
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
        } catch (Exception e) {
          // Skip if there's an issue accessing the bean definition
          continue;
        }
      }
    };
  }

  protected void registerReflectionTypeIfNecessary(Class<?> type, RuntimeHints hints) {
    String java = "java";
    if (!type.isPrimitive() && !type.getName().startsWith(java)) {
      hints.reflection().registerType(type, MemberCategory.values());
    }
  }

  protected void registerMapperRelationships(Class<?> mapperInterfaceType, RuntimeHints hints) {
    Method[] methods = ReflectionUtils.getAllDeclaredMethods(mapperInterfaceType);
    for (Method method : methods) {
      if (method.getDeclaringClass() != Object.class) {
        ReflectionUtils.makeAccessible(method);
        registerSqlProviderTypes(
            method,
            hints,
            org.apache.ibatis.annotations.SelectProvider.class,
            org.apache.ibatis.annotations.SelectProvider::value,
            org.apache.ibatis.annotations.SelectProvider::type);
        registerSqlProviderTypes(
            method,
            hints,
            org.apache.ibatis.annotations.InsertProvider.class,
            org.apache.ibatis.annotations.InsertProvider::value,
            org.apache.ibatis.annotations.InsertProvider::type);
        registerSqlProviderTypes(
            method,
            hints,
            org.apache.ibatis.annotations.UpdateProvider.class,
            org.apache.ibatis.annotations.UpdateProvider::value,
            org.apache.ibatis.annotations.UpdateProvider::type);
        registerSqlProviderTypes(
            method,
            hints,
            org.apache.ibatis.annotations.DeleteProvider.class,
            org.apache.ibatis.annotations.DeleteProvider::value,
            org.apache.ibatis.annotations.DeleteProvider::type);
        Class<?> returnType = resolveReturnClass(mapperInterfaceType, method);
        registerReflectionTypeIfNecessary(returnType, hints);
        resolveParameterClasses(mapperInterfaceType, method)
            .forEach(x -> registerReflectionTypeIfNecessary(x, hints));
      }
    }
  }

  protected <T extends java.lang.annotation.Annotation> void registerSqlProviderTypes(
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
