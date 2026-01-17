package cn.huava.common.graalvm;

import cn.huava.common.annotation.UnreachableForTesting;
import cn.huava.common.annotation.VisibleForTesting;
import cn.huava.common.enumeration.AccessModifierEnum;
import org.apache.commons.logging.LogFactory;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.ResolvableType;
import org.springframework.util.ClassUtils;

/**
 * Post-processor for MyBatis mapper factory beans.
 *
 * @author Camio1945
 */
@NullMarked
public class MyBatisMapperFactoryBeanPostProcessor
    implements MergedBeanDefinitionPostProcessor, BeanFactoryAware {

  private static final org.apache.commons.logging.Log LOG =
      LogFactory.getLog(MyBatisMapperFactoryBeanPostProcessor.class);

  private static final String MAPPER_FACTORY_BEAN = "org.mybatis.spring.mapper.MapperFactoryBean";

  private @Nullable ConfigurableBeanFactory beanFactory;

  @Override
  public void setBeanFactory(BeanFactory beanFactory) {
    this.beanFactory = (ConfigurableBeanFactory) beanFactory;
  }

  @Override
  public void postProcessMergedBeanDefinition(
      RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
    if (beanFactory == null) {
      return;
    }
    if (ClassUtils.isPresent(MAPPER_FACTORY_BEAN, beanFactory.getBeanClassLoader())) {
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

  protected @Nullable Class<?> getMapperInterface(@Nullable RootBeanDefinition beanDefinition) {
    try {
      if (beanDefinition == null) {
        return null;
      }
      return (Class<?>) beanDefinition.getPropertyValues().get("mapperInterface");
    } catch (Exception e) {
      LOG.debug("Fail getting mapper interface type.", e);
      return null;
    }
  }
}
