package cn.huava.sys.service.sysuser;

import static org.dromara.hutool.core.text.CharSequenceUtil.format;

import cn.huava.common.auth.AuthConstant;
import cn.huava.sys.mapper.SysUserMapper;
import cn.huava.sys.pojo.po.SysUserPo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.login.FailedLoginException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.codec.binary.Base64;
import org.dromara.hutool.http.HttpUtil;
import org.dromara.hutool.http.client.Request;
import org.dromara.hutool.http.client.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * refresh token
 *
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
class SysUserRefreshTokenService extends ServiceImpl<SysUserMapper, SysUserPo> {

  @Value("${server.port}")
  private int serverPort;

  @Value("${project.main_oauth2_client_id}")
  private String mainOauth2ClientId;

  @Value("${project.main_oauth2_secret}")
  private String mainOauth2Secret;

  protected String refreshToken(@NonNull String refreshToken) throws FailedLoginException {
    Request request = buildRequest(refreshToken);
    try (Response response = request.send()) {
      String body = response.bodyStr();
      if (response.isOk()) {
        return body;
      } else {
        throw new FailedLoginException("login failed: " + body);
      }
    } catch (Exception e) {
      throw new FailedLoginException("login failed: " + e.getMessage());
    }
  }

  private Request buildRequest(@NonNull String refreshToken) {
    String grantType = AuthConstant.REFRESH_TOKEN;
    String url = format("http://localhost:{}/oauth2/token", serverPort);
    Request request = HttpUtil.createPost(url);
    final Map<String, Object> formMap = HashMap.newHashMap(2);
    formMap.put(AuthConstant.GRANT_TYPE, grantType);
    formMap.put(AuthConstant.REFRESH_TOKEN, refreshToken);
    request.form(formMap);
    String authorization = format("{}:{}", mainOauth2ClientId, mainOauth2Secret);
    request.header(AuthConstant.AUTHORIZATION, "Basic " + Base64.encode(authorization));
    return request;
  }
}
