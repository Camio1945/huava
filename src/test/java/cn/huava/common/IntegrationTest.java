package cn.huava.common;

import static org.junit.jupiter.api.Assertions.*;

import cn.huava.common.pojo.dto.ApiResponseDataDto;
import cn.huava.sys.pojo.dto.SysUserJwtDto;
import cn.huava.sys.pojo.po.SysUserPo;
import cn.huava.sys.pojo.qo.LoginQo;
import java.io.IOException;
import org.dromara.hutool.core.data.id.IdUtil;
import org.dromara.hutool.core.reflect.TypeReference;
import org.dromara.hutool.core.text.StrValidator;
import org.dromara.hutool.http.HttpUtil;
import org.dromara.hutool.http.client.Request;
import org.dromara.hutool.http.client.Response;
import org.dromara.hutool.http.meta.Method;
import org.dromara.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;

/**
 * @author Camio1945
 */
class IntegrationTest {
  // private static final String BASE_URL = "http://localhost:22345";
  private static final String BASE_URL = "http://192.168.0.100:22345";

  private static final TypeReference<ApiResponseDataDto<String>> STR_TYPE =
      new TypeReference<>() {};

  private static String accessToken;

  private static String refreshToken;

  private static Long createdUserId;

  @Test
  void integrationTest() throws IOException {
    userTest();
  }

  private void userTest() throws IOException {
    code();
    captcha();
    login();
    refreshToken();
    getUserById();
    createUser();
    updateUser();
    patchUser();
    deleteUserById();
  }

  void code() {
    String resp = HttpUtil.get(BASE_URL + "/sys/user/code");
    ApiResponseDataDto<String> dto = JSONUtil.toBean(resp, STR_TYPE);
    assertEquals(0, dto.getCode());
    assertTrue(dto.getData().startsWith("http"));
  }

  void captcha() {
    String resp = HttpUtil.get(BASE_URL + "/captcha");
    assertNotNull(resp);
    assertTrue(resp.length() > 128);
  }

  void login() throws IOException {
    Request req = HttpUtil.createPost(BASE_URL + "/sys/user/login");
    LoginQo loginQo = new LoginQo("admin", "123456", "", true);
    req.body(JSONUtil.toJsonStr(loginQo));
    try (Response resp = req.send()) {
      String body = resp.bodyStr();
      TypeReference<ApiResponseDataDto<SysUserJwtDto>> type = new TypeReference<>() {};
      ApiResponseDataDto<SysUserJwtDto> dto = JSONUtil.toBean(body, type);
      assertEquals(0, dto.getCode());
      accessToken = dto.getData().getAccessToken();
      refreshToken = dto.getData().getRefreshToken();
      assertTrue(StrValidator.isNotBlank(accessToken));
      assertTrue(StrValidator.isNotBlank(refreshToken));
    }
  }

  void refreshToken() throws IOException {
    Request req = HttpUtil.createPost(BASE_URL + "/sys/user/refreshToken");
    req.body(refreshToken).contentType("text/plain");
    try (Response resp = req.send()) {
      String body = resp.bodyStr();
      ApiResponseDataDto<String> dto = JSONUtil.toBean(body, STR_TYPE);
      assertEquals(0, dto.getCode());
      assertTrue(StrValidator.isNotBlank(dto.getData()));
    }
  }

  void getUserById() throws IOException {
    SysUserPo po = getUserById(1L);
    assertEquals(1L, po.getId());
    assertNotNull(po.getUsername());
  }

  void createUser() throws IOException {
    Request req = HttpUtil.createPost(BASE_URL + "/sys/user/create");
    req.header("Authorization", "Bearer " + accessToken);
    SysUserPo po = new SysUserPo();
    po.setUsername(IdUtil.nanoId()).setPassword("123456").setIsEnabled(true);
    req.body(JSONUtil.toJsonStr(po));
    try (Response resp = req.send()) {
      String body = resp.bodyStr();
      TypeReference<ApiResponseDataDto<Long>> type = new TypeReference<>() {};
      ApiResponseDataDto<Long> dto = JSONUtil.toBean(body, type);
      assertEquals(0, dto.getCode());
      createdUserId = dto.getData();
      assertTrue(createdUserId > 0);
    }
  }

  void updateUser() throws IOException {
    Request req = HttpUtil.createRequest(BASE_URL + "/sys/user/update", Method.PUT);
    req.header("Authorization", "Bearer " + accessToken);
    SysUserPo po = getUserById(createdUserId);
    po.setRealName("Camio1945");
    po.setGender("M");
    req.body(JSONUtil.toJsonStr(po));
    try (Response resp = req.send()) {
      String body = resp.bodyStr();
      TypeReference<ApiResponseDataDto<Void>> type = new TypeReference<>() {};
      ApiResponseDataDto<Void> dto = JSONUtil.toBean(body, type);
      assertEquals(0, dto.getCode());
    }
  }

  void patchUser() throws IOException {
    // TODO
  }

  void deleteUserById() throws IOException {
    Request req =
        HttpUtil.createRequest(BASE_URL + "/sys/user/delete/" + createdUserId, Method.DELETE);
    req.header("Authorization", "Bearer " + accessToken);
    try (Response resp = req.send()) {
      String body = resp.bodyStr();
      TypeReference<ApiResponseDataDto<Void>> type = new TypeReference<>() {};
      ApiResponseDataDto<Void> dto = JSONUtil.toBean(body, type);
      assertEquals(0, dto.getCode());
    }
    req = HttpUtil.createGet(BASE_URL + "/sys/user/get/" + createdUserId);
    req.header("Authorization", "Bearer " + accessToken);
    try (Response resp = req.send()) {
      assertEquals(404, resp.getStatus());
    }
  }

  SysUserPo getUserById(Long id) throws IOException {
    Request req = HttpUtil.createGet(BASE_URL + "/sys/user/get/" + id);
    req.header("Authorization", "Bearer " + accessToken);
    try (Response resp = req.send()) {
      String body = resp.bodyStr();
      TypeReference<ApiResponseDataDto<SysUserPo>> type = new TypeReference<>() {};
      ApiResponseDataDto<SysUserPo> dto = JSONUtil.toBean(body, type);
      assertEquals(0, dto.getCode());
      return dto.getData();
    }
  }
}
