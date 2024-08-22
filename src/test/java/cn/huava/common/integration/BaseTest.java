package cn.huava.common.integration;

import static org.junit.jupiter.api.Assertions.*;

import cn.huava.common.pojo.dto.ResDto;
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

  protected static final TypeReference<ResDto<String>> STR_TYPE = new TypeReference<>() {};

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
      String body = resp.bodyStr();
      TypeReference<ResDto<UserJwtDto>> type = new TypeReference<>() {};
      ResDto<UserJwtDto> dto = JSONUtil.toBean(body, type);
      assertEquals(0, dto.getCode());
      accessToken = dto.getData().getAccessToken();
      refreshToken = dto.getData().getRefreshToken();
      assertTrue(Fn.isNotBlank(accessToken));
      assertTrue(Fn.isNotBlank(refreshToken));
    }
  }
}
