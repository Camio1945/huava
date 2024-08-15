package cn.huava.common.auth;

import static cn.huava.common.auth.AuthConstant.PASSWORD;
import static cn.huava.common.auth.AuthConstant.USERNAME;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.dromara.hutool.core.text.StrValidator;
import org.dromara.hutool.http.server.servlet.ServletUtil;
import org.dromara.hutool.json.JSONObject;
import org.dromara.hutool.json.JSONUtil;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;

/**
 * @author Camio1945
 */
public class SysPasswordAuthConverter implements AuthenticationConverter {

  @Nullable
  @Override
  public Authentication convert(HttpServletRequest request) {
    String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
    if (!AuthConstant.SYS_PASSWORD_GRANT_TYPE.equals(grantType)) {
      return null;
    }
    String body = ServletUtil.getBody(request);
    JSONObject bodyParams = JSONUtil.toBean(body, JSONObject.class);
    validateUsernameAndPasswordAreNotBlank(bodyParams);
    Map<String, Object> additionalParameters = buildAdditionalParameters(bodyParams);
    Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
    return new SysPasswordAuthToken(clientPrincipal, additionalParameters);
  }

  private static void validateUsernameAndPasswordAreNotBlank(JSONObject bodyParams) {
    if (StrValidator.isBlank(bodyParams.getStr(USERNAME))
        || StrValidator.isBlank(bodyParams.getStr(PASSWORD))) {
      throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_REQUEST);
    }
  }

  private static Map<String, Object> buildAdditionalParameters(JSONObject bodyParams) {
    Map<String, Object> additionalParameters = HashMap.newHashMap(bodyParams.size());
    for (Map.Entry<String, Object> entry : bodyParams) {
      String key = entry.getKey();
      additionalParameters.put(key, entry.getValue().toString());
    }
    return additionalParameters;
  }
}
