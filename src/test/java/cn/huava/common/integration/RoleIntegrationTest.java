package cn.huava.common.integration;

import static org.junit.jupiter.api.Assertions.*;

import cn.huava.common.pojo.dto.ResDto;
import cn.huava.sys.pojo.po.RolePo;
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
class RoleIntegrationTest extends BaseTest {
  private static Long createdRoleId;
  String name = IdUtil.nanoId(10);

  @Test
  void integrationTest() throws IOException {
    login();
    createRole();
    getRoleById();
    deleteRoleById();
  }

  void createRole() throws IOException {
    createRoleWithInvalidRoles();
    createRoleWithValidParams();
    createRoleWithSameName();
  }

  void getRoleById() throws IOException {
    RolePo po = getRoleById(1L);
    assertEquals(1L, po.getId());
    assertNotNull(po.getName());
  }

  void deleteRoleById() throws IOException {
    Request req =
        HttpUtil.createRequest(BASE_URL + "/sys/role/delete/" + createdRoleId, Method.DELETE);
    req.header("Authorization", "Bearer " + accessToken);
    try (Response resp = req.send()) {
      String body = resp.bodyStr();
      TypeReference<ResDto<Void>> type = new TypeReference<>() {};
      ResDto<Void> dto = JSONUtil.toBean(body, type);
      assertEquals(0, dto.getCode());
    }
    req = HttpUtil.createGet(BASE_URL + "/sys/role/get/" + createdRoleId);
    req.header("Authorization", "Bearer " + accessToken);
    try (Response resp = req.send()) {
      assertEquals(404, resp.getStatus());
    }
  }

  private static void createRoleWithInvalidRoles() throws IOException {
    RolePo po = new RolePo();
    Request req = HttpUtil.createPost(BASE_URL + "/sys/role/create");
    req.header("Authorization", "Bearer " + accessToken);
    testNoParam(req, po);
    testShortName(po, req);
    testLongNameAndDescription(po, req);
  }

  private void createRoleWithValidParams() throws IOException {
    Request req = HttpUtil.createPost(BASE_URL + "/sys/role/create");
    req.header("Authorization", "Bearer " + accessToken);
    RolePo po = new RolePo();
    po.setName(name).setDescription(name + "描述");
    req.body(JSONUtil.toJsonStr(po));
    try (Response resp = req.send()) {
      String body = resp.bodyStr();
      TypeReference<ResDto<Long>> type = new TypeReference<>() {};
      ResDto<Long> dto = JSONUtil.toBean(body, type);
      createdRoleId = dto.getData();
      assertEquals(0, dto.getCode());
    }
  }

  private void createRoleWithSameName() throws IOException {
    Request req = HttpUtil.createPost(BASE_URL + "/sys/role/create");
    req.header("Authorization", "Bearer " + accessToken);
    RolePo po = new RolePo();
    po.setName(name).setDescription(name + "描述");
    req.body(JSONUtil.toJsonStr(po));
    try (Response resp = req.send()) {
      assertEquals(400, resp.getStatus());
      System.out.println(resp.bodyStr());
    }
  }

  RolePo getRoleById(Long id) throws IOException {
    Request req = HttpUtil.createGet(BASE_URL + "/sys/role/get/" + id);
    req.header("Authorization", "Bearer " + accessToken);
    try (Response resp = req.send()) {
      String body = resp.bodyStr();
      TypeReference<ResDto<RolePo>> type = new TypeReference<>() {};
      ResDto<RolePo> dto = JSONUtil.toBean(body, type);
      assertEquals(0, dto.getCode());
      return dto.getData();
    }
  }

  private static void testNoParam(Request req, RolePo po) throws IOException {
    req.body(JSONUtil.toJsonStr(po));
    // if parameters are not valid, the response status code will be 400
    try (Response resp = req.send()) {
      assertEquals(400, resp.getStatus());
      System.out.println(resp.bodyStr());
    }
  }

  private static void testShortName(RolePo po, Request req) throws IOException {
    po.setName("aa");
    req.body(JSONUtil.toJsonStr(po));
    try (Response resp = req.send()) {
      assertEquals(400, resp.getStatus());
      System.out.println(resp.bodyStr());
    }
  }

  private static void testLongNameAndDescription(RolePo po, Request req) throws IOException {
    String name = "012345678901234567890123456789";
    po.setName(name);
    po.setDescription(name.repeat(10));
    req.body(JSONUtil.toJsonStr(po));
    try (Response resp = req.send()) {
      assertEquals(400, resp.getStatus());
      System.out.println(resp.bodyStr());
    }
  }

  boolean isNameExists(@NonNull final String name) throws IOException {
    Request req = HttpUtil.createGet(BASE_URL + "/sys/role/isNameExists?name=" + name);
    req.header("Authorization", "Bearer " + accessToken);
    try (Response resp = req.send()) {
      String body = resp.bodyStr();
      TypeReference<ResDto<Boolean>> type = new TypeReference<>() {};
      ResDto<Boolean> dto = JSONUtil.toBean(body, type);
      assertEquals(0, dto.getCode());
      return dto.getData();
    }
  }
}
