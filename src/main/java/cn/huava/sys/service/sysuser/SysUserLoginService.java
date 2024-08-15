package cn.huava.sys.service.sysuser;

import static org.dromara.hutool.core.text.CharSequenceUtil.format;

import cn.huava.common.auth.AuthConstant;
import cn.huava.sys.mapper.SysUserMapper;
import cn.huava.sys.pojo.po.SysUser;
import cn.huava.sys.pojo.qo.LoginQo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.io.IOException;
import javax.security.auth.login.FailedLoginException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.codec.binary.Base64;
import org.dromara.hutool.http.HttpUtil;
import org.dromara.hutool.http.client.Request;
import org.dromara.hutool.http.client.Response;
import org.dromara.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * login
 *
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
class SysUserLoginService extends ServiceImpl<SysUserMapper, SysUser> {

  @Value("${server.port}")
  private int serverPort;

  @Value("${project.main_oauth2_client_id}")
  private String mainOauth2ClientId;

  @Value("${project.main_oauth2_secret}")
  private String mainOauth2Secret;

  protected String login(LoginQo loginQo) throws IOException, FailedLoginException {
    Request request = buildRequest(loginQo);
    try (Response response = request.send()) {
      String body = response.bodyStr();
      if (response.isOk()) {
        return body;
      } else {
        throw new FailedLoginException("login failed: " + body);
      }
    }
  }

  private Request buildRequest(LoginQo loginQo) {
    String grantType = AuthConstant.SYS_PASSWORD_GRANT_TYPE;
    String url = format("http://localhost:{}/oauth2/token?grant_type={}", serverPort, grantType);
    Request request = HttpUtil.createPost(url);
    String authorization = format("{}:{}", mainOauth2ClientId, mainOauth2Secret);
    request.header(AuthConstant.AUTHORIZATION, "Basic " + Base64.encode(authorization));
    request.body(JSONUtil.toJsonStr(loginQo));
    return request;
  }
}
