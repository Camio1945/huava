package cn.huava.common.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Camio1945
 */
@Configuration
@EnableMethodSecurity
@AllArgsConstructor
public class PasswordEncoderConfig {

  /**
   * This bean cannot be in {@link SecurityConfig} class; otherwise, it will generate circular
   * dependencies.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
