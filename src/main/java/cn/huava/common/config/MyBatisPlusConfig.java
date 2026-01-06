package cn.huava.common.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.*;

/**
 * MyBatisPlus 配置
 *
 * @author Camio1945
 */
@Configuration
@AllArgsConstructor
public class MyBatisPlusConfig {

  /**
   * 分页拦截器<br>
   * 注：这个 Bean 不能写在 {@link SecurityConfig} 类中，否则会出现循环依赖的问题
   */
  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
    return interceptor;
  }
}
