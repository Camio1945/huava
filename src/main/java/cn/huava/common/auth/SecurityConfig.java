package cn.huava.common.auth;

import static org.springframework.security.config.Customizer.withDefaults;

import cn.huava.common.jackson.module.HuavaJacksonModule;
import cn.huava.common.util.EncryptUtil;
import cn.huava.common.util.KeyUtil;
import cn.huava.sys.auth.SysUserDetails;
import cn.huava.sys.service.SysJdbcOauth2Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.server.servlet.OAuth2AuthorizationServerProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService.OAuth2AuthorizationParametersMapper;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.*;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.oauth2.server.authorization.settings.*;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author Camio1945
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
  private final SysPasswordAuthProvider sysPasswordAuthProvider;

  @Value("${project.main_oauth2_client_id}")
  private String mainOauth2ClientId;

  @Value("${project.main_oauth2_secret}")
  private String mainOauth2Secret;

  @Value("${project.rsa_public_key}")
  private String rsaPublicKey;

  @Value("${project.rsa_private_key}")
  private String rsaPrivateKey;

  /**
   * Defines an oauth2 authorization server (see the `OAuth2AuthorizationServerConfigurer` part).
   * <br>
   * Because we are using our own grant type {@link AuthConstant#SYS_PASSWORD_GRANT_TYPE}, <br>
   * we need a {@link SysPasswordAuthConverter} to convert the {@link
   * AuthConstant#SYS_PASSWORD_GRANT_TYPE} request to an authentication token. <br>
   * And we need to provide a {@link SysPasswordAuthProvider} to generate access token and refresh
   * token. <br>
   * Note 1: This filter chain only handles oauth2 requests (see the `http.securityMatcher` part),
   * not all request, so we need {@link SecurityConfig#appSecurityFilterChain(HttpSecurity)} too.
   * Note 2: You can see the oauth2 requests in here: {@link
   * OAuth2AuthorizationServerProperties.Endpoint}
   *
   * @param http the {@link HttpSecurity} instance to be configured
   * @return the {@link SecurityFilterChain} instance
   * @throws Exception exceptions are nearly never thrown
   */
  @Bean
  @Order(1)
  public SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
    OAuth2AuthorizationServerConfigurer configurer = new OAuth2AuthorizationServerConfigurer();
    http.securityMatcher(configurer.getEndpointsMatcher())
        .csrf(AbstractHttpConfigurer::disable)
        .with(configurer, withDefaults());
    http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
        .tokenEndpoint(
            oauth2 ->
                oauth2
                    .accessTokenRequestConverter(new SysPasswordAuthConverter())
                    .authenticationProvider(sysPasswordAuthProvider));
    return http.build();
  }

  /**
   * Defines an oauth2 resource server (see the `oauth2ResourceServer` part). <br>
   * Handles all requests except oauth2 requests ({@link
   * OAuth2AuthorizationServerProperties.Endpoint}). <br>
   *
   * @param http the {@link HttpSecurity} instance to be configured
   * @return the {@link SecurityFilterChain} instance
   * @throws Exception exceptions are nearly never thrown
   */
  @Bean
  @Order(2)
  public SecurityFilterChain appSecurityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            authorize -> {
              authorize.requestMatchers(AuthConstant.LOGIN_URI).permitAll();
              authorize.requestMatchers(AuthConstant.REFRESH_TOKEN_URI).permitAll();
              authorize.requestMatchers("/temp/test/").permitAll();
              authorize.anyRequest().authenticated();
            })
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
        .build();
  }

  /** Needed in {@link SysPasswordAuthProvider} to save authentication token in database. */
  @Bean
  public OAuth2AuthorizationService authorizationService(
      JdbcTemplate jdbcTemplate, RegisteredClientRepository repository) {
    SysJdbcOauth2Service authorizationService =
        new SysJdbcOauth2Service(jdbcTemplate, repository);
    OAuth2AuthorizationRowMapper rowMapper = new OAuth2AuthorizationRowMapper(repository);
    authorizationService.setAuthorizationRowMapper(rowMapper);
    authorizationService.setAuthorizationParametersMapper(authorizationParametersMapper(rowMapper));
    return authorizationService;
  }

  private static OAuth2AuthorizationParametersMapper authorizationParametersMapper(
      JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper rowMapper) {
    ObjectMapper objectMapper = new ObjectMapper();
    ClassLoader classLoader = SysJdbcOauth2Service.class.getClassLoader();
    List<com.fasterxml.jackson.databind.Module> securityModules =
        SecurityJackson2Modules.getModules(classLoader);
    objectMapper.registerModules(securityModules);
    objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
    objectMapper.registerModules(new HuavaJacksonModule());
    rowMapper.setObjectMapper(objectMapper);
    OAuth2AuthorizationParametersMapper oauth2AuthorizationParametersMapper =
        new OAuth2AuthorizationParametersMapper();
    oauth2AuthorizationParametersMapper.setObjectMapper(objectMapper);
    return oauth2AuthorizationParametersMapper;
  }

  /** Needed in {@link SysPasswordAuthProvider} to save generate token. */
  @Bean
  public OAuth2TokenGenerator<OAuth2Token> tokenGenerator() {
    NimbusJwtEncoder jwtEncoder = new NimbusJwtEncoder(jwkSource());
    JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
    jwtGenerator.setJwtCustomizer(tokenCustomizer());
    OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
    OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
    return new DelegatingOAuth2TokenGenerator(
        jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
  }

  /** Provide jwk source which contains RSA key. */
  @Bean
  public JWKSource<SecurityContext> jwkSource() {
    RSAKey rsaKey = buildRsaKey();
    JWKSet jwkSet = new JWKSet(rsaKey);
    //noinspection unused
    return (jwkSelector, context) -> jwkSelector.select(jwkSet);
  }

  /** We need to store extra info in the access token, like username (encrypted) */
  @Bean
  public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
    return context -> {
      OAuth2ClientAuthenticationToken principal = context.getPrincipal();
      SysUserDetails user = (SysUserDetails) principal.getDetails();
      if (context.getTokenType().getValue().equals(AuthConstant.ACCESS_TOKEN)) {
        context.getClaims().claim("username", EncryptUtil.encrypt(user.getUsername()));
      }
    };
  }

  private RSAKey buildRsaKey() {
    KeyPair keyPair = KeyUtil.getKeyPair(rsaPublicKey, rsaPrivateKey);
    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
    return new RSAKey.Builder(publicKey).privateKey(privateKey).build();
  }

  /**
   * A repository for registered client.<br>
   * Needed in {@link SecurityConfig#authorizationService(JdbcTemplate,
   * RegisteredClientRepository)}, but not only there.
   */
  @Bean
  public RegisteredClientRepository registeredClientRepository() {
    RegisteredClient registeredClient =
        RegisteredClient.withId(AuthConstant.REGISTERED_CLIENT_ID)
            .clientId(mainOauth2ClientId)
            .clientSecret(passwordEncoder().encode(mainOauth2Secret))
            .scope("read")
            .scope(OidcScopes.OPENID)
            .scope(OidcScopes.PROFILE)
            .scope("message.read")
            .scope("message.write")
            .scope("read")
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .authorizationGrantType(
                new AuthorizationGrantType(AuthConstant.SYS_PASSWORD_GRANT_TYPE))
            .tokenSettings(tokenSettings())
            .build();
    return new InMemoryRegisteredClientRepository(registeredClient);
  }

  /** Needed for encrypt password. (asymmetric encryption) */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /** Token setting, like the expiry time */
  @Bean
  public TokenSettings tokenSettings() {
    return TokenSettings.builder()
        .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
        .accessTokenTimeToLive(Duration.ofHours(1))
        .refreshTokenTimeToLive(Duration.ofDays(30))
        .build();
  }
}
