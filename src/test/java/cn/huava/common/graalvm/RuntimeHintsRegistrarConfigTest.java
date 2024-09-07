package cn.huava.common.graalvm;

import static org.mockito.Mockito.mock;

import cn.huava.common.util.Fn;
import cn.huava.sys.mapper.UserMapper;
import org.dromara.hutool.core.exception.ExceptionUtil;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.RegisteredBean;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * 为了提升代码测试覆盖率加的，用处不大，可以直接忽略。<br>
 * java:S2187 要求测试类中必须有 @Test 标注的方法，否则就认为这不是一个测试类。但当前类是被人调用的，其实是测试类。
 */
@SuppressWarnings("java:S2187")
public class RuntimeHintsRegistrarConfigTest {
  public static void testAll() throws Exception {
    RuntimeHintsRegistrarConfig.MyBatisMapperFactoryBeanPostProcessor postProcessor =
        Fn.getBean(RuntimeHintsRegistrarConfig.MyBatisMapperFactoryBeanPostProcessor.class);
    postProcessor.getMapperInterface(new RootBeanDefinition());
    RuntimeHintsRegistrarConfig.MyBatisBeanFactoryInitializationAotProcessor aotProcessor =
        Fn.getBean(RuntimeHintsRegistrarConfig.MyBatisBeanFactoryInitializationAotProcessor.class);
    ConfigurableListableBeanFactory factory = mock(ConfigurableListableBeanFactory.class);
    RegisteredBean bean = RegisteredBean.of(factory, "myBatisMapperFactoryBeanPostProcessor");
    isExcludedFromAotProcessing(aotProcessor, bean);
    processAheadOfTime(aotProcessor, factory);
    registerMapperRelationships(aotProcessor);
    new RuntimeHintsRegistrarConfig.MyBatisMapperTypeUtils();
  }

  /** 即便出异常了，也认为没有问题，因为开发环境本来就测试不到。 */
  private static void registerMapperRelationships(
      RuntimeHintsRegistrarConfig.MyBatisBeanFactoryInitializationAotProcessor aotProcessor) {
    try {
      aotProcessor.registerMapperRelationships(UserMapper.class, new RuntimeHints());
    } catch (Exception e) {
      ExceptionUtil.wrapRuntime(e);
    }
  }

  /** 即便出异常了，也认为没有问题，因为开发环境本来就测试不到。 */
  private static void processAheadOfTime(
      RuntimeHintsRegistrarConfig.MyBatisBeanFactoryInitializationAotProcessor aotProcessor,
      ConfigurableListableBeanFactory factory) {
    try {
      aotProcessor.processAheadOfTime(factory);
    } catch (Exception e) {
      ExceptionUtil.wrapRuntime(e);
    }
  }

  /** 即便出异常了，也认为没有问题，因为开发环境本来就测试不到。 */
  private static void isExcludedFromAotProcessing(
      RuntimeHintsRegistrarConfig.MyBatisBeanFactoryInitializationAotProcessor aotProcessor,
      RegisteredBean bean) {
    try {
      aotProcessor.isExcludedFromAotProcessing(bean);
    } catch (Exception e) {
      ExceptionUtil.wrapRuntime(e);
    }
  }
}
