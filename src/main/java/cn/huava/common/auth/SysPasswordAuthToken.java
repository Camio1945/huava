package cn.huava.common.auth;

import java.io.Serial;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

/**
 * @author Camio1945
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class SysPasswordAuthToken extends OAuth2AuthorizationGrantAuthenticationToken {

  @Serial private static final long serialVersionUID = 1L;

  private String username = "";

  private String password = "";

  public SysPasswordAuthToken(
      Authentication clientPrincipal,
      @Nullable Map<String, Object> additionalParameters) {
    super(
        new AuthorizationGrantType(AuthConstant.SYS_PASSWORD_GRANT_TYPE),
        clientPrincipal,
        additionalParameters);
    if (additionalParameters != null) {
      this.username = (String) additionalParameters.get("username");
      this.password = (String) additionalParameters.get("password");
    }
  }
}
