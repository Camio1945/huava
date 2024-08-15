package cn.huava.common.jackson.mixin;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

/**
 * @author Camio1945
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class AuthorizationGrantTypeMixin {

  /**
   * corresponding to {@link AuthorizationGrantType#AuthorizationGrantType(java.lang.String) }
   *
   * @param value the value of the grant type
   */
  @JsonCreator
  AuthorizationGrantTypeMixin(@SuppressWarnings("unused") @JsonProperty("value") String value) {}
}
