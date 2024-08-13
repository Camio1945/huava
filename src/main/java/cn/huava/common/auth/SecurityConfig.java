package cn.huava.common.auth;

import static org.springframework.security.config.Customizer.withDefaults;

import cn.huava.common.KeyUtil;
import cn.huava.sys.auth.SysUserUserDetails;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.*;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.*;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 安全配置
 *
 * @author Camio1945
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
  private final SysPasswordAuthProvider sysPasswordAuthProvider;

  @Value("${cn.huava.main_client_id}")
  private String mainClientId;

  @Value("${cn.huava.main_secret}")
  private String mainSecret;

  @Value("${cn.huava.rsa_public_key}")
  private String rsaPublicKey;

  @Value("${cn.huava.rsa_private_key}")
  private String rsaPrivateKey;

  @Bean
  @Order(1)
  public SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
    OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
        new OAuth2AuthorizationServerConfigurer();
    http.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
        .csrf(AbstractHttpConfigurer::disable)
        .with(authorizationServerConfigurer, withDefaults());
    http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
        .tokenEndpoint(
            oauth2 ->
                oauth2
                    .accessTokenRequestConverter(new SysPasswordAuthConverter())
                    .authenticationProvider(sysPasswordAuthProvider));
    return http.build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain appSecurityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            authorize -> {
              authorize.requestMatchers(AuthConstant.LOGIN_URI).permitAll();
              authorize.requestMatchers("/temp/test/").permitAll();
              authorize.anyRequest().authenticated();
            })
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
        .build();
  }

  @Bean
  public OAuth2AuthorizationService authorizationService() {
    return new InMemoryOAuth2AuthorizationService();
  }

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

  @Bean
  public JWKSource<SecurityContext> jwkSource() {
    RSAKey rsaKey = buildRsaKey();
    JWKSet jwkSet = new JWKSet(rsaKey);
    //noinspection unused
    return (jwkSelector, context) -> jwkSelector.select(jwkSet);
  }

  @Bean
  public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
    return context -> {
      OAuth2ClientAuthenticationToken principal = context.getPrincipal();
      SysUserUserDetails user = (SysUserUserDetails) principal.getDetails();
      Set<String> authorities =
          user.getAuthorities().stream()
              .map(GrantedAuthority::getAuthority)
              .collect(Collectors.toSet());
      if (context.getTokenType().getValue().equals(AuthConstant.ACCESS_TOKEN)) {
        context.getClaims().claim("authorities", authorities).claim("user", user.getUsername());
      }
    };
  }

  private RSAKey buildRsaKey() {
    KeyPair keyPair = KeyUtil.getKeyPair(rsaPublicKey, rsaPrivateKey);
    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
    return new RSAKey.Builder(publicKey).privateKey(privateKey).build();
  }

  @Bean
  public OAuth2AuthorizationConsentService oAuth2AuthorizationConsentService() {
    return new InMemoryOAuth2AuthorizationConsentService();
  }

  @Bean
  public RegisteredClientRepository registeredClientRepository() {
    RegisteredClient registeredClient =
        RegisteredClient.withId("client")
            .clientId(mainClientId)
            .clientSecret(passwordEncoder().encode(mainSecret))
            .scope("read")
            .scope(OidcScopes.OPENID)
            .scope(OidcScopes.PROFILE)
            .scope("message.read")
            .scope("message.write")
            .scope("read")
            .redirectUri("http://todo")
            // .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            // .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            // .authorizationGrantType(AuthorizationGrantType.JWT_BEARER)
            .authorizationGrantType(
                new AuthorizationGrantType(AuthConstant.SYS_PASSWORD_GRANT_TYPE))
            .tokenSettings(tokenSettings())
            .clientSettings(clientSettings())
            .build();

    return new InMemoryRegisteredClientRepository(registeredClient);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public TokenSettings tokenSettings() {
    return TokenSettings.builder()
        .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
        .accessTokenTimeToLive(Duration.ofDays(1))
        .build();
  }

  @Bean
  public ClientSettings clientSettings() {
    return ClientSettings.builder().build();
  }

  @Bean
  public AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings.builder().build();
  }

  @Bean
  public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
    return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
  }
}
