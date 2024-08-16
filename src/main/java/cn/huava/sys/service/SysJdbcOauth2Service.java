package cn.huava.sys.service;

import cn.huava.sys.pojo.po.Oauth2AuthorizationPo;
import cn.huava.sys.service.oauth2authorization.Oauth2AuthorizationAceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.hutool.extra.spring.SpringUtil;
import org.dromara.hutool.json.JSONUtil;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

/**
 * @author Camio1945
 */
public class SysJdbcOauth2Service extends JdbcOAuth2AuthorizationService {

  public SysJdbcOauth2Service(
      JdbcOperations jdbcOperations, RegisteredClientRepository repository) {
    super(jdbcOperations, repository);
  }

  @Nullable
  @Override
  public OAuth2Authorization findById(String id) {
    Oauth2AuthorizationAceService service = getOauth2AuthorizationAceService();
    Oauth2AuthorizationPo po = service.getById(id);
    if (po == null) {
      return null;
    }
    return Oauth2AuthorizationConverter.convert(po);
  }

  @Nullable
  @Override
  public OAuth2Authorization findByToken(String token, @Nullable OAuth2TokenType tokenType) {
    if (tokenType == null) {
      throw new IllegalArgumentException("tokenType cannot be null");
    }
    Oauth2AuthorizationAceService service = getOauth2AuthorizationAceService();
    LambdaQueryWrapper<Oauth2AuthorizationPo> queryWrapper = getQueryWrapper(token, tokenType);
    Oauth2AuthorizationPo po = service.getOne(queryWrapper);
    if (po == null) {
      return null;
    }
    // TODO Here has a huge bug, it works fine when running with java, but when running with GraalVM native image, the res will lack a lot of things
    OAuth2Authorization res = Oauth2AuthorizationConverter.convert(po);
    System.out.println("\nThe OAuth2Authorization from findByToken:\n");
    System.out.println(JSONUtil.toJsonPrettyStr(res));
    return res;
  }

  private static LambdaQueryWrapper<Oauth2AuthorizationPo> getQueryWrapper(
      String token, OAuth2TokenType tokenType) {
    LambdaQueryWrapper<Oauth2AuthorizationPo> queryWrapper = new LambdaQueryWrapper<>();
    if (OAuth2ParameterNames.STATE.equals(tokenType.getValue())) {
      queryWrapper.eq(Oauth2AuthorizationPo::getState, token);
    } else if (OAuth2ParameterNames.CODE.equals(tokenType.getValue())) {
      queryWrapper.eq(Oauth2AuthorizationPo::getAuthorizationCodeValue, token);
    } else if (OAuth2TokenType.ACCESS_TOKEN.equals(tokenType)) {
      queryWrapper.eq(Oauth2AuthorizationPo::getAccessTokenValue, token);
    } else if (OidcParameterNames.ID_TOKEN.equals(tokenType.getValue())) {
      queryWrapper.eq(Oauth2AuthorizationPo::getOidcIdTokenValue, token);
    } else if (OAuth2TokenType.REFRESH_TOKEN.equals(tokenType)) {
      queryWrapper.eq(Oauth2AuthorizationPo::getRefreshTokenValue, token);
    } else if (OAuth2ParameterNames.USER_CODE.equals(tokenType.getValue())) {
      queryWrapper.eq(Oauth2AuthorizationPo::getUserCodeValue, token);
    } else if (OAuth2ParameterNames.DEVICE_CODE.equals(tokenType.getValue())) {
      queryWrapper.eq(Oauth2AuthorizationPo::getDeviceCodeValue, token);
    }
    queryWrapper.last("limit 1");
    return queryWrapper;
  }

  private Oauth2AuthorizationAceService getOauth2AuthorizationAceService() {
    return SpringUtil.getBean(Oauth2AuthorizationAceService.class);
  }
}
