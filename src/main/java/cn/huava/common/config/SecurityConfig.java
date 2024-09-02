package cn.huava.common.config;

import static cn.huava.common.constant.CommonConstant.REFRESH_TOKEN_URI;

import cn.huava.common.filter.JwtAuthenticationFilter;
import cn.huava.common.filter.UriAuthFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 安全配置
 *
 * @author Camio1945
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final UriAuthFilter uriAuthFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            registry -> {
              // registry is the type of
              // AuthorizeHttpRequestsConfigurer$AuthorizationManagerRequestMatcherRegistry
              registry.requestMatchers("/captcha").permitAll();
              registry.requestMatchers("/sys/user/login").permitAll();
              registry.requestMatchers(REFRESH_TOKEN_URI).permitAll();
              registry.requestMatchers("/temp/test/**").permitAll();
              registry.requestMatchers(getImagesMatcher()).permitAll();
              registry.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
              registry.anyRequest().authenticated();
            })
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(uriAuthFilter, JwtAuthenticationFilter.class)
        .build();
  }

  private RegexRequestMatcher getImagesMatcher() {
    return new RegexRequestMatcher(
        ".*/.*\\.(?i)(jpg|jpeg|png|gif|bmp|tiff)$", HttpMethod.GET.name());
  }

  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("*"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
      throws Exception {
    return authConfig.getAuthenticationManager();
  }
}
