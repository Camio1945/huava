package cn.huava.common.config;

import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.*;

/**
 * MyBatisPlus configuration. <br>
 *
 * @author Camio1945
 */
@Configuration
@AllArgsConstructor
public class MyBatisPlusConfig {

  /**
   * This bean cannot be in {@link SecurityConfig} class; otherwise, it will generate circular
   * dependencies.
   */
  @Bean
  public PaginationInnerInterceptor paginationInnerInterceptor() {
    return new PaginationInnerInterceptor();
  }
}
