package cn.huava.common.auth;

import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClaimAccessor;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.stereotype.Component;

/**
 * @author Camio1945
 */
@Component
public class SysPasswordAuthProvider implements AuthenticationProvider {

  private static final String ERROR_URI =
      "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";
  private final OAuth2AuthorizationService authorizationService;
  private final UserDetailsService userDetailsService;
  private final PasswordEncoder passwordEncoder;
  private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;

  public SysPasswordAuthProvider(
      @Lazy OAuth2AuthorizationService authorizationService,
      UserDetailsService userDetailsService,
      @Lazy PasswordEncoder passwordEncoder,
      @Lazy OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) {
    this.authorizationService = authorizationService;
    this.userDetailsService = userDetailsService;
    this.passwordEncoder = passwordEncoder;
    this.tokenGenerator = tokenGenerator;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    SysPasswordAuthToken sysPasswordAuthToken = (SysPasswordAuthToken) authentication;
    String username = sysPasswordAuthToken.getUsername();
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    validatePassword(sysPasswordAuthToken, userDetails, username);

    // -----------Create a new Security Context Holder Context----------
    setNewContext(userDetails);
    // -----------TOKEN BUILDERS----------
    OAuth2ClientAuthenticationToken clientPrincipal =
        buildAuthenticatedClient(sysPasswordAuthToken);
    RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();
    assert registeredClient != null;
    Set<String> authorizedScopes = buildAuthorizedScopes(userDetails, registeredClient);
    DefaultOAuth2TokenContext.Builder tokenContextBuilder =
        getTokenContextBuilder(
            registeredClient, clientPrincipal, authorizedScopes, sysPasswordAuthToken);
    OAuth2Authorization.Builder authorizationBuilder =
        buildAuthorizationBuilder(registeredClient, clientPrincipal, authorizedScopes);

    // -----------ACCESS TOKEN----------
    OAuth2AccessToken accessToken = buildAccessToken(tokenContextBuilder, authorizationBuilder);

    // -----------REFRESH TOKEN----------
    OAuth2RefreshToken refreshToken =
        buildRefreshToken(
            registeredClient, clientPrincipal, tokenContextBuilder, authorizationBuilder);

    OAuth2Authorization authorization = authorizationBuilder.build();
    this.authorizationService.save(authorization);

    return new OAuth2AccessTokenAuthenticationToken(
        registeredClient, clientPrincipal, accessToken, refreshToken);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return SysPasswordAuthToken.class.isAssignableFrom(authentication);
  }

  private void validatePassword(
      SysPasswordAuthToken sysPasswordAuthToken, UserDetails userDetails, String username) {
    String password = sysPasswordAuthToken.getPassword();
    if (!username.equals(userDetails.getUsername())
        || !passwordEncoder.matches(password, userDetails.getPassword())) {
      throw new OAuth2AuthenticationException(OAuth2ErrorCodes.ACCESS_DENIED);
    }
  }

  private static void setNewContext(UserDetails userDetails) {
    OAuth2ClientAuthenticationToken oAuth2ClientAuthenticationToken =
        (OAuth2ClientAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    oAuth2ClientAuthenticationToken.setDetails(userDetails);
    SecurityContext newContext = SecurityContextHolder.createEmptyContext();
    newContext.setAuthentication(oAuth2ClientAuthenticationToken);
    SecurityContextHolder.setContext(newContext);
  }

  private static OAuth2ClientAuthenticationToken buildAuthenticatedClient(
      Authentication authentication) {
    OAuth2ClientAuthenticationToken clientPrincipal = null;
    if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(
        authentication.getPrincipal().getClass())) {
      clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
    }
    if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
      return clientPrincipal;
    }
    throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
  }

  private static Set<String> buildAuthorizedScopes(
      UserDetails userDetails, RegisteredClient registeredClient) {
    return userDetails.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .filter(scope -> registeredClient.getScopes().contains(scope))
        .collect(Collectors.toSet());
  }

  private static DefaultOAuth2TokenContext.Builder getTokenContextBuilder(
      RegisteredClient registeredClient,
      OAuth2ClientAuthenticationToken clientPrincipal,
      Set<String> authorizedScopes,
      SysPasswordAuthToken sysPasswordAuthToken) {
    return DefaultOAuth2TokenContext.builder()
        .registeredClient(registeredClient)
        .principal(clientPrincipal)
        .authorizationServerContext(AuthorizationServerContextHolder.getContext())
        .authorizedScopes(authorizedScopes)
        .authorizationGrantType(new AuthorizationGrantType(AuthConstant.SYS_PASSWORD_GRANT_TYPE))
        .authorizationGrant(sysPasswordAuthToken);
  }

  private static OAuth2Authorization.Builder buildAuthorizationBuilder(
      RegisteredClient registeredClient,
      OAuth2ClientAuthenticationToken clientPrincipal,
      Set<String> authorizedScopes) {
    return OAuth2Authorization.withRegisteredClient(registeredClient)
        .attribute(Principal.class.getName(), clientPrincipal)
        .principalName(clientPrincipal.getName())
        .authorizationGrantType(new AuthorizationGrantType(AuthConstant.SYS_PASSWORD_GRANT_TYPE))
        .authorizedScopes(authorizedScopes);
  }

  private OAuth2AccessToken buildAccessToken(
      DefaultOAuth2TokenContext.Builder tokenContextBuilder,
      OAuth2Authorization.Builder authorizationBuilder) {
    OAuth2TokenContext tokenContext =
        tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
    OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);
    if (generatedAccessToken == null) {
      OAuth2Error error =
          new OAuth2Error(
              OAuth2ErrorCodes.SERVER_ERROR,
              "The token generator failed to generate the access token.",
              ERROR_URI);
      throw new OAuth2AuthenticationException(error);
    }
    OAuth2AccessToken accessToken =
        new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER,
            generatedAccessToken.getTokenValue(),
            generatedAccessToken.getIssuedAt(),
            generatedAccessToken.getExpiresAt(),
            tokenContext.getAuthorizedScopes());
    if (generatedAccessToken instanceof ClaimAccessor claimAccessor) {
      authorizationBuilder.token(
          accessToken,
          metadata ->
              metadata.put(
                  OAuth2Authorization.Token.CLAIMS_METADATA_NAME, claimAccessor.getClaims()));
    } else {
      authorizationBuilder.accessToken(accessToken);
    }
    return accessToken;
  }

  private OAuth2RefreshToken buildRefreshToken(
      RegisteredClient registeredClient,
      OAuth2ClientAuthenticationToken clientPrincipal,
      DefaultOAuth2TokenContext.Builder tokenContextBuilder,
      OAuth2Authorization.Builder authorizationBuilder) {
    OAuth2RefreshToken refreshToken = null;
    if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN)
        && !clientPrincipal
            .getClientAuthenticationMethod()
            .equals(ClientAuthenticationMethod.NONE)) {

      OAuth2TokenContext tokenContext =
          tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
      OAuth2Token generatedRefreshToken = this.tokenGenerator.generate(tokenContext);
      if (!(generatedRefreshToken instanceof OAuth2RefreshToken)) {
        OAuth2Error error =
            new OAuth2Error(
                OAuth2ErrorCodes.SERVER_ERROR,
                "The token generator failed to generate the refresh token.",
                ERROR_URI);
        throw new OAuth2AuthenticationException(error);
      }
      refreshToken = (OAuth2RefreshToken) generatedRefreshToken;
      authorizationBuilder.refreshToken(refreshToken);
    }
    return refreshToken;
  }
}
