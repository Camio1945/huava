package cn.huava.common.graalvm;

import cn.huava.common.annotation.UnreachableForTesting;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

/**
 * 当前类以及它所包含的 {@link NativeRuntimeHintsRegistrar} 类都是用来构建 GraalVM native image 的。<br>
 * 当前类的主要功能是为了让 MyBatis-Plus 支持 GraalVM native image。<br>
 * 参考自 <a href="https://github.com/nieqiurong/mybatis-native-demo">mybatis-native-demo</a> 。
 *
 * @author Camio1945
 */
@Configuration
@UnreachableForTesting("很多代码在单元测试时执行不到，因为那就是在 GraalVM native image 里面调用的。")
@ImportRuntimeHints(NativeRuntimeHintsRegistrar.class)
public class RuntimeHintsRegistrarConfig {

  /**
   * 这个 bean 是必需的，否则会出现以下错误：
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
   * 这个 bean 是必需的，否则会出现以下错误：
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
}
