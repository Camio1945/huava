package cn.huava.common.jackson.deserializer;

import cn.huava.common.util.JsonNodeUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

/**
 * @author Camio1945
 */
public class TokenSettingsDeserializer extends JsonDeserializer<TokenSettings> {

  @Override
  public TokenSettings deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext)
      throws IOException {
    ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
    JsonNode jsonNode = mapper.readTree(jsonParser);

    Map<String, Object> settings =
        JsonNodeUtil.findValue(jsonNode, "settings", JsonNodeUtil.STRING_OBJECT_MAP, mapper);

    return TokenSettings.withSettings(settings).build();
  }
}
