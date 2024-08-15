package cn.huava.common.jackson.mixin;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

/**
 * @author Camio1945
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class ClientAuthenticationMethodMixin {

  /**
   * corresponding to {@link ClientAuthenticationMethod#ClientAuthenticationMethod(String)}
   *
   * @param value the value of the client authentication method
   */
  @JsonCreator
  ClientAuthenticationMethodMixin(
      @SuppressWarnings("unused") @JsonProperty("value") String value) {}
}
