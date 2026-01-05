package cn.huava.common.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码加密器配置
 *
 * @author Camio1945
 */
@Configuration
@EnableMethodSecurity
@AllArgsConstructor
public class PasswordEncoderConfig {

  /**
   * 密码加密器<br>
   * 注：这个 Bean 不能写在 {@link SecurityConfig} 类中，否则会出现循环依赖的问题
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
