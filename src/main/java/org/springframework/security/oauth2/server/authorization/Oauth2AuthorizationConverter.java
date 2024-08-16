package org.springframework.security.oauth2.server.authorization;

import cn.huava.sys.auth.SysUserDetails;
import cn.huava.sys.pojo.po.Oauth2AuthorizationPo;
import cn.huava.sys.pojo.po.SysUserPo;
import java.time.Duration;
import org.dromara.hutool.json.*;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.*;

/**
 * Convert {@link cn.huava.sys.pojo.po.Oauth2AuthorizationPo} to {@link OAuth2Authorization}. <br>
 * This class has to be in this package because of the protected members in {@link
 * OAuth2Authorization}.
 *
 * @author Camio1945
 */
public class Oauth2AuthorizationConverter {
  private Oauth2AuthorizationConverter() {}

  public static OAuth2Authorization convert(Oauth2AuthorizationPo po) {
    OAuth2Authorization.Builder builder =
        new OAuth2Authorization.Builder(po.getRegisteredClientId());
    String attributesJsonStr = new String(po.getAttributes());
    JSONObject attributesJson = JSONUtil.parseObj(attributesJsonStr);
    String principalKey = "java.security.Principal";
    JSONObject principalJson = attributesJson.getJSONObject(principalKey);
    OAuth2ClientAuthenticationToken clientAuthenticationToken =
        buildClientAuthenticationToken(principalJson);
    builder
        .principalName(po.getPrincipalName())
        .authorizationGrantType(new AuthorizationGrantType(po.getAuthorizationGrantType()))
        .id(po.getId())
        .token(buildOauth2AccessToken(po))
        .token(buildOauth2RefreshToken(po))
        .attribute(principalKey, clientAuthenticationToken);
    return builder.build();
  }

  private static OAuth2ClientAuthenticationToken buildClientAuthenticationToken(
      JSONObject principalJson) {
    OAuth2ClientAuthenticationToken clientAuthenticationToken =
        new OAuth2ClientAuthenticationToken(
            buildRegisteredClient(principalJson),
            ClientAuthenticationMethod.CLIENT_SECRET_BASIC,
            principalJson.getStr("credentials"));
    clientAuthenticationToken.setAuthenticated(principalJson.getBool("authenticated"));
    JSONObject detailsJson = principalJson.getJSONObject("details");
    SysUserPo sysUserPo = new SysUserPo();
    sysUserPo.setLoginName(detailsJson.getStr("username"));
    sysUserPo.setPassword(detailsJson.getStr("password"));
    SysUserDetails details = new SysUserDetails(sysUserPo);
    clientAuthenticationToken.setDetails(details);
    return clientAuthenticationToken;
  }

  private static OAuth2AccessToken buildOauth2AccessToken(Oauth2AuthorizationPo po) {
    return new OAuth2AccessToken(
        OAuth2AccessToken.TokenType.BEARER,
        new String(po.getAccessTokenValue()),
        po.getAccessTokenIssuedAt().toInstant(),
        po.getAccessTokenExpiresAt().toInstant(),
        null);
  }

  private static OAuth2RefreshToken buildOauth2RefreshToken(Oauth2AuthorizationPo po) {
    return new OAuth2RefreshToken(
        new String(po.getRefreshTokenValue()),
        po.getRefreshTokenIssuedAt().toInstant(),
        po.getRefreshTokenExpiresAt().toInstant());
  }

  private static RegisteredClient buildRegisteredClient(JSONObject principalJson) {
    JSONObject registeredClientJson = principalJson.getJSONObject("registeredClient");
    RegisteredClient.Builder registerdClientBuilder =
        initRegisterdClientBuilder(registeredClientJson);
    setAuthorizationGrantType(registeredClientJson, registerdClientBuilder);
    setScopes(registeredClientJson, registerdClientBuilder);
    setClientSettings(registeredClientJson, registerdClientBuilder);
    setTokenSettings(registeredClientJson, registerdClientBuilder);
    return registerdClientBuilder.build();
  }

  private static RegisteredClient.Builder initRegisterdClientBuilder(
      JSONObject registeredClientJson) {
    return RegisteredClient.withId(registeredClientJson.getStr("id"))
        .clientId(registeredClientJson.getStr("clientId"))
        .clientSecret(registeredClientJson.getStr("clientSecret"))
        .clientName(registeredClientJson.getStr("clientName"))
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
  }

  private static void setAuthorizationGrantType(
      JSONObject registeredClientJson, RegisteredClient.Builder registerdClientBuilder) {
    JSONArray authorizationGrantTypesJsonArray =
        registeredClientJson.getJSONArray("authorizationGrantTypes");
    if (authorizationGrantTypesJsonArray.size() > 1) {
      authorizationGrantTypesJsonArray = authorizationGrantTypesJsonArray.getJSONArray(1);
      for (Object o : authorizationGrantTypesJsonArray) {
        JSONObject authorizationGrantTypeJson = (JSONObject) o;
        String value = authorizationGrantTypeJson.getStr("value");
        AuthorizationGrantType authorizationGrantType = new AuthorizationGrantType(value);
        registerdClientBuilder.authorizationGrantType(authorizationGrantType);
      }
    }
  }

  private static void setScopes(
      JSONObject registeredClientJson, RegisteredClient.Builder registerdClientBuilder) {
    JSONArray scopesJsonArray = registeredClientJson.getJSONArray("scopes");
    if (scopesJsonArray.size() > 1) {
      scopesJsonArray = scopesJsonArray.getJSONArray(1);
      for (int i = 0; i < scopesJsonArray.size(); i++) {
        registerdClientBuilder.scope(scopesJsonArray.getStr(i));
      }
    }
  }

  private static void setClientSettings(
      JSONObject registeredClientJson, RegisteredClient.Builder registerdClientBuilder) {
    JSONObject clientSettingsJson = registeredClientJson.getJSONObject("clientSettings");
    JSONObject settingsJson = clientSettingsJson.getJSONObject("settings");
    ClientSettings.Builder clientSettingsBuilder = ClientSettings.builder();
    clientSettingsBuilder.requireProofKey(
        settingsJson.getBool("settings.client.require-proof-key"));
    clientSettingsBuilder.requireAuthorizationConsent(
        settingsJson.getBool("settings.client.require-authorization-consent"));
    ClientSettings clientSettings = clientSettingsBuilder.build();
    registerdClientBuilder.clientSettings(clientSettings);
  }

  private static void setTokenSettings(
      JSONObject registeredClientJson, RegisteredClient.Builder registerdClientBuilder) {
    JSONObject tokenSettingsJson = registeredClientJson.getJSONObject("tokenSettings");
    JSONObject settingsJson = tokenSettingsJson.getJSONObject("settings");
    TokenSettings.Builder tokenSettingBuilder = TokenSettings.builder();
    idTokenSignatureAlgorithm(settingsJson, tokenSettingBuilder);
    accessTokenTimeToLive(settingsJson, tokenSettingBuilder);
    accessTokenFormat(settingsJson, tokenSettingBuilder);
    refreshTokenTimeToLive(settingsJson, tokenSettingBuilder);
    authorizationCodeTimeToLive(settingsJson, tokenSettingBuilder);
    deviceCodeTimeToLive(settingsJson, tokenSettingBuilder);
    TokenSettings tokenSettings = tokenSettingBuilder.build();
    registerdClientBuilder.tokenSettings(tokenSettings);
  }

  private static void idTokenSignatureAlgorithm(
      JSONObject settingsJson, TokenSettings.Builder tokenSettingBuilder) {
    JSONArray algorithmJsonArr =
        settingsJson.getJSONArray("settings.token.id-token-signature-algorithm");
    if (algorithmJsonArr.size() > 1) {
      String algorithm = algorithmJsonArr.getStr(1);
      tokenSettingBuilder.idTokenSignatureAlgorithm(SignatureAlgorithm.from(algorithm));
    }
  }

  private static void accessTokenTimeToLive(
      JSONObject settingsJson, TokenSettings.Builder tokenSettingBuilder) {
    JSONArray accessTokenTimeToLiveJsonArray =
        settingsJson.getJSONArray("settings.token.access-token-time-to-live");
    if (accessTokenTimeToLiveJsonArray.size() > 1) {
      Duration accessTokenTimeToLive = Duration.ofSeconds(accessTokenTimeToLiveJsonArray.getInt(1));
      tokenSettingBuilder.accessTokenTimeToLive(accessTokenTimeToLive);
    }
  }

  private static void accessTokenFormat(
      JSONObject settingsJson, TokenSettings.Builder tokenSettingBuilder) {
    JSONObject accessTokenFormatJson =
        settingsJson.getJSONObject("settings.token.access-token-format");
    OAuth2TokenFormat oAuth2TokenFormat =
        new OAuth2TokenFormat(accessTokenFormatJson.getStr("value"));
    tokenSettingBuilder.accessTokenFormat(oAuth2TokenFormat);
  }

  private static void refreshTokenTimeToLive(
      JSONObject settingsJson, TokenSettings.Builder tokenSettingBuilder) {
    JSONArray refreshTokenTimeToLiveJsonArray =
        settingsJson.getJSONArray("settings.token.refresh-token-time-to-live");
    if (refreshTokenTimeToLiveJsonArray.size() > 1) {
      Duration refreshTokenTimeToLive =
          Duration.ofSeconds(refreshTokenTimeToLiveJsonArray.getInt(1));
      tokenSettingBuilder.refreshTokenTimeToLive(refreshTokenTimeToLive);
    }
  }

  private static void authorizationCodeTimeToLive(
      JSONObject settingsJson, TokenSettings.Builder tokenSettingBuilder) {
    JSONArray authorizationCodeTimeToLiveJsonArray =
        settingsJson.getJSONArray("settings.token.authorization-code-time-to-live");
    if (authorizationCodeTimeToLiveJsonArray.size() > 1) {
      Duration authorizationCodeTimeToLive =
          Duration.ofSeconds(authorizationCodeTimeToLiveJsonArray.getInt(1));
      tokenSettingBuilder.authorizationCodeTimeToLive(authorizationCodeTimeToLive);
    }
  }

  private static void deviceCodeTimeToLive(
      JSONObject settingsJson, TokenSettings.Builder tokenSettingBuilder) {
    JSONArray deviceCodeTimeToLiveJsonArray =
        settingsJson.getJSONArray("settings.token.device-code-time-to-live");
    if (deviceCodeTimeToLiveJsonArray.size() > 1) {
      Duration deviceCodeTimeToLive = Duration.ofSeconds(deviceCodeTimeToLiveJsonArray.getInt(1));
      tokenSettingBuilder.deviceCodeTimeToLive(deviceCodeTimeToLive);
    }
  }
}
