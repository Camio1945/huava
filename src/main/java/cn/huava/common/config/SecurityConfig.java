package cn.huava.common.config;

import static cn.huava.common.constant.CommonConstant.REFRESH_TOKEN_URI;

import cn.huava.common.filter.JwtAuthFilter;
import cn.huava.common.filter.UriAuthFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
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
  private final JwtAuthFilter jwtAuthenticationFilter;
  private final UriAuthFilter uriAuthFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
        // 禁用 CSRF，前后端分离的项目用不到 CSRF，用了反而会使正常的写请求报 403 错误，更多原因见 README-v.0.0.6.md
        .csrf(AbstractHttpConfigurer::disable)
        // 注册哪些 uri 需要认证，哪些不需要
        .authorizeHttpRequests(
            registry -> {
              // 注册不需要验证 token 的（自由的） uri
              registerFreeUris(registry);
              // 其他请求需要认证
              registry.anyRequest().authenticated();
            })
        // 添加 JWT 认证过滤器，Spring 要求必须指定顺序，这里随便指定到 CorsFilter 的后面，但不一定非要是 CorsFilter
        .addFilterAfter(jwtAuthenticationFilter, CorsFilter.class)
        // 添加 URI 认证过滤器，用于验证用户是否有权限访问某个 URI，必须在 JwtAuthenticationFilter 之后
        .addFilterAfter(uriAuthFilter, JwtAuthFilter.class)
        .build();
  }

  /** 允许跨域访问，因为本来就是对外提供接口的 */
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

  /** 注册不需要验证 token 的（自由的） uri */
  private void registerFreeUris(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
          registry) {
    // 验证码
    registry.requestMatchers("/captcha").permitAll();
    // 登录
    registry.requestMatchers("/sys/user/login").permitAll();
    // 刷新 token
    registry.requestMatchers(REFRESH_TOKEN_URI).permitAll();
    // 临时测试
    registry.requestMatchers("/temp/test/**").permitAll();
    // 各种后缀的图片（比如用户头像）
    registry.requestMatchers(getImagesMatcher()).permitAll();
    // 所有的 OPTIONS 请求
    registry.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
  }

  /** 匹配各种图片后缀的 uri */
  private RegexRequestMatcher getImagesMatcher() {
    return new RegexRequestMatcher(
        ".*/.*\\.(?i)(jpg|jpeg|png|gif|bmp|tiff)$", HttpMethod.GET.name());
  }
}
