package cn.huava.common.integration;

import static org.junit.jupiter.api.Assertions.*;

import cn.huava.common.util.Fn;
import cn.huava.sys.pojo.dto.UserJwtDto;
import cn.huava.sys.pojo.qo.LoginQo;
import java.io.IOException;
import org.dromara.hutool.core.reflect.TypeReference;
import org.dromara.hutool.http.HttpUtil;
import org.dromara.hutool.http.client.Request;
import org.dromara.hutool.http.client.Response;
import org.dromara.hutool.json.JSONUtil;

/**
 * @author Camio1945
 */
abstract class BaseTest {
  protected static final String BASE_URL = "http://localhost:22345";
  // private static final String BASE_URL = "http://192.168.0.100:22345";

  protected static String accessToken;

  protected static String refreshToken;

  void login() throws IOException {
    Request req = HttpUtil.createPost(BASE_URL + "/sys/user/login");
    LoginQo loginQo =
        new LoginQo()
            .setUsername("admin")
            .setPassword("123456")
            .setIsCaptchaDisabledForTesting(true);
    req.body(JSONUtil.toJsonStr(loginQo));
    try (Response resp = req.send()) {
      assertEquals(200, resp.getStatus());
      String body = resp.bodyStr();
      TypeReference<UserJwtDto> type = new TypeReference<>() {};
      UserJwtDto dto = JSONUtil.toBean(body, type);
      accessToken = dto.getAccessToken();
      refreshToken = dto.getRefreshToken();
      assertTrue(Fn.isNotBlank(accessToken));
      assertTrue(Fn.isNotBlank(refreshToken));
    }
  }
}
