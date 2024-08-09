package cn.huava.common.config;

import static org.springframework.security.config.Customizer.withDefaults;

// import cn.huava.sys.auth.PreAuthenticatedAuthenticationProvider;
// import cn.huava.sys.service.SysUserLoginUserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.RememberMeConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;

import javax.sql.DataSource;

/**
 * 安全配置
 *
 * @author Camio1945
 */
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
  private final DataSource dataSource;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
        .authorizeHttpRequests(
            registry -> {
              // registry is the type of
              // AuthorizeHttpRequestsConfigurer$AuthorizationManagerRequestMatcherRegistry
              registry.requestMatchers("/temp/test/").permitAll();
              registry.requestMatchers("/").permitAll();
              registry.anyRequest().authenticated();
            })
        .formLogin(withDefaults())
        .rememberMe(rememberMeCustomizer())
        .build();
  }

  private Customizer<RememberMeConfigurer<HttpSecurity>> rememberMeCustomizer() {
    return rememberMeConfigurer -> rememberMeConfigurer.tokenRepository(jdbcTokenRepository());
  }

  @Bean
  public JdbcTokenRepositoryImpl jdbcTokenRepository() {
    JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
    tokenRepository.setDataSource(dataSource);
    return tokenRepository;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
