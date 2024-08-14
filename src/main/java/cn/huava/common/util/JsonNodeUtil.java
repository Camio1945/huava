package cn.huava.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

/**
 * @author Camio1945
 */
public class JsonNodeUtil {
  public static final TypeReference<Instant> INSTANT = new TypeReference<>() {};

  public static final TypeReference<Set<String>> STRING_SET = new TypeReference<>() {};

  public static final TypeReference<Map<String, Object>> STRING_OBJECT_MAP =
      new TypeReference<>() {};

  private JsonNodeUtil() {}

  public static String findStringValue(JsonNode jsonNode, String fieldName) {
    if (jsonNode == null) {
      return null;
    }
    JsonNode value = jsonNode.findValue(fieldName);
    return (value != null && value.isTextual()) ? value.asText() : null;
  }

  public static <T> T findValue(
      JsonNode jsonNode,
      String fieldName,
      TypeReference<T> valueTypeReference,
      ObjectMapper mapper) {
    if (jsonNode == null) {
      return null;
    }
    JsonNode value = jsonNode.findValue(fieldName);
    if (value != null && value.isContainerNode()) {
      return mapper.convertValue(value, valueTypeReference);
    }
    return null;
  }
}
