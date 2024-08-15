package cn.huava.common.jackson.deserializer;

import cn.huava.common.util.JsonNodeUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Instant;
import java.util.Set;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

/**
 * @author Camio1945
 */
public class RegisteredClientDeserializer extends JsonDeserializer<RegisteredClient> {

  private static final TypeReference<Set<ClientAuthenticationMethod>>
      CLIENT_AUTHENTICATION_METHOD_SET = new TypeReference<>() {};
  private static final TypeReference<Set<AuthorizationGrantType>> AUTHORIZATION_GRANT_TYPE_SET =
      new TypeReference<>() {};

  @Override
  public RegisteredClient deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
    JsonNode root = mapper.readTree(jsonParser);
    return deserialize(mapper, root);
  }

  private RegisteredClient deserialize(ObjectMapper mapper, JsonNode root) {
    String id = JsonNodeUtil.findStringValue(root, "id");
    String clientId = JsonNodeUtil.findStringValue(root, "clientId");
    String clientSecret = JsonNodeUtil.findStringValue(root, "clientSecret");
    String clientName = JsonNodeUtil.findStringValue(root, "clientName");
    Set<String> scopes = JsonNodeUtil.findValue(root, "scopes", JsonNodeUtil.STRING_SET, mapper);
    return RegisteredClient.withId(id)
        .clientId(clientId)
        .clientIdIssuedAt(getClientIdIssuedAt(mapper, root))
        .clientSecret(clientSecret)
        .clientSecretExpiresAt(getClientSecretExpiresAt(mapper, root))
        .clientName(clientName)
        .clientAuthenticationMethods(
            methods -> methods.addAll(getClientAuthenticationMethods(mapper, root)))
        .authorizationGrantTypes(types -> types.addAll(getAuthorizationGrantTypes(mapper, root)))
        .redirectUris(uris -> uris.addAll(getRedirectUris(mapper, root)))
        .scopes(s -> s.addAll(scopes))
        .clientSettings(getClientSettings(mapper, root))
        .tokenSettings(getTokenSettings(mapper, root))
        .build();
  }

  private static Instant getClientIdIssuedAt(ObjectMapper mapper, JsonNode root) {
    return JsonNodeUtil.findValue(root, "clientIdIssuedAt", JsonNodeUtil.INSTANT, mapper);
  }

  private static Instant getClientSecretExpiresAt(ObjectMapper mapper, JsonNode root) {
    return JsonNodeUtil.findValue(root, "clientSecretExpiresAt", JsonNodeUtil.INSTANT, mapper);
  }

  private static Set<ClientAuthenticationMethod> getClientAuthenticationMethods(
      ObjectMapper mapper, JsonNode root) {
    return JsonNodeUtil.findValue(
        root, "clientAuthenticationMethods", CLIENT_AUTHENTICATION_METHOD_SET, mapper);
  }

  private static Set<AuthorizationGrantType> getAuthorizationGrantTypes(
      ObjectMapper mapper, JsonNode root) {
    return JsonNodeUtil.findValue(
        root, "authorizationGrantTypes", AUTHORIZATION_GRANT_TYPE_SET, mapper);
  }

  private static Set<String> getRedirectUris(ObjectMapper mapper, JsonNode root) {
    return JsonNodeUtil.findValue(root, "redirectUris", JsonNodeUtil.STRING_SET, mapper);
  }

  private static ClientSettings getClientSettings(ObjectMapper mapper, JsonNode root) {
    return JsonNodeUtil.findValue(
        root, "clientSettings", new TypeReference<ClientSettings>() {}, mapper);
  }

  private static TokenSettings getTokenSettings(ObjectMapper mapper, JsonNode root) {
    return JsonNodeUtil.findValue(
        root, "tokenSettings", new TypeReference<TokenSettings>() {}, mapper);
  }
}
