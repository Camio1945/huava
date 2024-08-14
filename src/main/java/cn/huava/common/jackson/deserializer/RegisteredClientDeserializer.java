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
      JsonParser jsonParser, DeserializationContext deserializationContext)
      throws IOException {
    ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
    JsonNode root = mapper.readTree(jsonParser);
    return deserialize(mapper, root);
  }

  private RegisteredClient deserialize(ObjectMapper mapper, JsonNode root) {
    String id = JsonNodeUtil.findStringValue(root, "id");
    String clientId = JsonNodeUtil.findStringValue(root, "clientId");
    Instant clientIdIssuedAt =
        JsonNodeUtil.findValue(root, "clientIdIssuedAt", JsonNodeUtil.INSTANT, mapper);
    String clientSecret = JsonNodeUtil.findStringValue(root, "clientSecret");
    Instant clientSecretExpiresAt =
        JsonNodeUtil.findValue(root, "clientSecretExpiresAt", JsonNodeUtil.INSTANT, mapper);
    String clientName = JsonNodeUtil.findStringValue(root, "clientName");

    Set<ClientAuthenticationMethod> clientAuthenticationMethods =
        JsonNodeUtil.findValue(
            root, "clientAuthenticationMethods", CLIENT_AUTHENTICATION_METHOD_SET, mapper);
    Set<AuthorizationGrantType> authorizationGrantTypes =
        JsonNodeUtil.findValue(
            root, "authorizationGrantTypes", AUTHORIZATION_GRANT_TYPE_SET, mapper);
    Set<String> redirectUris =
        JsonNodeUtil.findValue(root, "redirectUris", JsonNodeUtil.STRING_SET, mapper);
    Set<String> scopes = JsonNodeUtil.findValue(root, "scopes", JsonNodeUtil.STRING_SET, mapper);
    ClientSettings clientSettings =
        JsonNodeUtil.findValue(
            root, "clientSettings", new TypeReference<ClientSettings>() {}, mapper);
    TokenSettings tokenSettings =
        JsonNodeUtil.findValue(
            root, "tokenSettings", new TypeReference<TokenSettings>() {}, mapper);

    return RegisteredClient.withId(id)
        .clientId(clientId)
        .clientIdIssuedAt(clientIdIssuedAt)
        .clientSecret(clientSecret)
        .clientSecretExpiresAt(clientSecretExpiresAt)
        .clientName(clientName)
        .clientAuthenticationMethods(methods -> methods.addAll(clientAuthenticationMethods))
        .authorizationGrantTypes(types -> types.addAll(authorizationGrantTypes))
        .redirectUris(uris -> uris.addAll(redirectUris))
        .scopes(s -> s.addAll(scopes))
        .clientSettings(clientSettings)
        .tokenSettings(tokenSettings)
        .build();
  }
}
