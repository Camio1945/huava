package cn.huava.common.jackson.deserializer;

import cn.huava.common.util.JsonNodeUtil;
import cn.huava.sys.auth.SysUserDetails;
import cn.huava.sys.pojo.po.SysUser;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import java.io.IOException;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

/**
 * @author Camio1945
 */
public class Oauth2ClientAuthenticationTokenDeserializer
    extends JsonDeserializer<OAuth2ClientAuthenticationToken> {

  @Override
  public OAuth2ClientAuthenticationToken deserialize(
      JsonParser jsonParser, DeserializationContext context) throws IOException {
    ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
    JsonNode jsonNode = mapper.readTree(jsonParser);
    return deserialize(mapper, jsonNode);
  }

  private OAuth2ClientAuthenticationToken deserialize(ObjectMapper mapper, JsonNode root) {
    RegisteredClient registeredClient =
        JsonNodeUtil.findValue(root, "registeredClient", new TypeReference<>() {}, mapper);
    JsonNode userJson = root.get("details");
    SysUser sysUser = new SysUser();
    sysUser.setLoginName(userJson.get("username").asText());
    sysUser.setPassword(userJson.get("password").asText());
    SysUserDetails details = new SysUserDetails(sysUser);

    String credentials = JsonNodeUtil.findStringValue(root, "credentials");
    ClientAuthenticationMethod clientAuthenticationMethod =
        JsonNodeUtil.findValue(
            root, "clientAuthenticationMethod", new TypeReference<>() {}, mapper);

    OAuth2ClientAuthenticationToken clientAuthenticationToken =
        new OAuth2ClientAuthenticationToken(
            registeredClient, clientAuthenticationMethod, credentials);
    clientAuthenticationToken.setDetails(details);
    return clientAuthenticationToken;
  }
}
