package cn.huava.common.config;

import static org.springframework.security.config.Customizer.withDefaults;

// import cn.huava.sys.auth.PreAuthenticatedAuthenticationProvider;
// import cn.huava.sys.service.SysUserLoginUserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

/**
 * 安全配置
 *
 * @author Camio1945
 */
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
  // private final SysUserLoginUserDetailsServiceImpl sysUserLoginUserDetailsService;
  // private final PreAuthenticatedAuthenticationProvider preProvider;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
        .authorizeHttpRequests(
            registry -> {
              // registry is
              // AuthorizeHttpRequestsConfigurer$AuthorizationManagerRequestMatcherRegistry
              registry.requestMatchers("/temp/test/").permitAll();
              registry.requestMatchers("/").permitAll();
              registry.anyRequest().authenticated();
            })
        // .authenticationProvider(preProvider)
        // .oauth2Login(withDefaults())
        .formLogin(withDefaults())
        // .httpBasic(withDefaults())
        .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // @Bean
  // public DaoAuthenticationProvider authenticationProvider(UserDetailsService myUserService) {
  //   DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
  //   auth.setUserDetailsService(myUserService);
  //   // auth.setPasswordEncoder(passwordEncoder());
  //   return auth;
  // }

  // @Bean
  // public UserDetailsService userDetailsService() {
  //   return sysUserLoginUserDetailsService;
  // }

  // @Bean
  // public UserDetailsService userDetailsService() {
  //   return new InMemoryUserDetailsManager(
  //       User.withDefaultPasswordEncoder()
  //           .username("user")
  //           .password("password")
  //           .roles("USER")
  //           .build());
  // }
}
