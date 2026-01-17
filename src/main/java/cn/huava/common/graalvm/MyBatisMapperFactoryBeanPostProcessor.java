package cn.huava.common.graalvm;

import cn.huava.common.annotation.UnreachableForTesting;
import cn.huava.common.annotation.VisibleForTesting;
import cn.huava.common.enumeration.AccessModifierEnum;
import org.apache.commons.logging.LogFactory;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.ResolvableType;
import org.springframework.util.ClassUtils;
import org.jspecify.annotations.NonNull;

/**
 * Post-processor for MyBatis mapper factory beans.
 *
 * @author Camio1945
 */
public class MyBatisMapperFactoryBeanPostProcessor
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

  @UnreachableForTesting("第二个 if 分支的代码只在 GraalVM native image 编译时才会执行到")
  @VisibleForTesting(original = AccessModifierEnum.PRIVATE)
  protected void resolveMapperFactoryBeanTypeIfNecessary(RootBeanDefinition beanDefinition) {
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

  protected Class<?> getMapperInterface(RootBeanDefinition beanDefinition) {
    try {
      return (Class<?>) beanDefinition.getPropertyValues().get("mapperInterface");
    } catch (Exception e) {
      LOG.debug("Fail getting mapper interface type.", e);
      return null;
    }
  }
}