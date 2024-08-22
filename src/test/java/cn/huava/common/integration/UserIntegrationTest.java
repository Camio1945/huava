package cn.huava.common.integration;

import static org.junit.jupiter.api.Assertions.*;

import cn.huava.common.pojo.dto.ResDto;
import cn.huava.common.util.Fn;
import cn.huava.sys.pojo.po.RolePo;
import cn.huava.sys.pojo.po.UserPo;
import java.io.IOException;
import lombok.NonNull;
import org.dromara.hutool.core.data.id.IdUtil;
import org.dromara.hutool.core.reflect.TypeReference;
import org.dromara.hutool.http.HttpUtil;
import org.dromara.hutool.http.client.Request;
import org.dromara.hutool.http.client.Response;
import org.dromara.hutool.http.meta.Method;
import org.dromara.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;

/**
 * @author Camio1945
 */
class UserIntegrationTest extends BaseTest {

  private static Long createdUserId;

  @Test
  void integrationTest() throws IOException {
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
    ResDto<String> dto = JSONUtil.toBean(resp, STR_TYPE);
    assertEquals(0, dto.getCode());
    assertTrue(dto.getData().startsWith("http"));
  }

  void captcha() {
    String resp = HttpUtil.get(BASE_URL + "/captcha");
    assertNotNull(resp);
    assertTrue(resp.length() > 128);
  }

  void refreshToken() throws IOException {
    Request req = HttpUtil.createPost(BASE_URL + "/sys/user/refreshToken");
    req.body(refreshToken).contentType("text/plain");
    try (Response resp = req.send()) {
      String body = resp.bodyStr();
      ResDto<String> dto = JSONUtil.toBean(body, STR_TYPE);
      assertEquals(0, dto.getCode());
      assertTrue(Fn.isNotBlank(dto.getData()));
    }
  }

  void getUserById() throws IOException {
    UserPo po = getUserById(1L);
    assertEquals(1L, po.getId());
    assertNotNull(po.getUsername());
  }

  void createUser() throws IOException {
    Request req = HttpUtil.createPost(BASE_URL + "/sys/user/create");
    req.header("Authorization", "Bearer " + accessToken);
    UserPo po = new UserPo();
    po.setUsername(IdUtil.nanoId()).setPassword("123456").setIsEnabled(true);
    req.body(JSONUtil.toJsonStr(po));
    try (Response resp = req.send()) {
      String body = resp.bodyStr();
      TypeReference<ResDto<Long>> type = new TypeReference<>() {};
      ResDto<Long> dto = JSONUtil.toBean(body, type);
      assertEquals(0, dto.getCode());
      createdUserId = dto.getData();
      assertTrue(createdUserId > 0);
    }
  }

  void updateUser() throws IOException {
    Request req = HttpUtil.createRequest(BASE_URL + "/sys/user/update", Method.PUT);
    req.header("Authorization", "Bearer " + accessToken);
    UserPo po = getUserById(createdUserId);
    po.setRealName("Camio1945");
    po.setGender("M");
    req.body(JSONUtil.toJsonStr(po));
    try (Response resp = req.send()) {
      String body = resp.bodyStr();
      TypeReference<ResDto<Void>> type = new TypeReference<>() {};
      ResDto<Void> dto = JSONUtil.toBean(body, type);
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
      TypeReference<ResDto<Void>> type = new TypeReference<>() {};
      ResDto<Void> dto = JSONUtil.toBean(body, type);
      assertEquals(0, dto.getCode());
    }
    req = HttpUtil.createGet(BASE_URL + "/sys/user/get/" + createdUserId);
    req.header("Authorization", "Bearer " + accessToken);
    try (Response resp = req.send()) {
      assertEquals(404, resp.getStatus());
    }
  }

  UserPo getUserById(Long id) throws IOException {
    Request req = HttpUtil.createGet(BASE_URL + "/sys/user/get/" + id);
    req.header("Authorization", "Bearer " + accessToken);
    try (Response resp = req.send()) {
      String body = resp.bodyStr();
      TypeReference<ResDto<UserPo>> type = new TypeReference<>() {};
      ResDto<UserPo> dto = JSONUtil.toBean(body, type);
      assertEquals(0, dto.getCode());
      return dto.getData();
    }
  }

}
