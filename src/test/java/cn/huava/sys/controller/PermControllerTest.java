package cn.huava.sys.controller;

import static cn.huava.sys.controller.ApiTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cn.huava.sys.enumeration.PermTypeEnum;
import cn.huava.sys.pojo.po.PermPo;
import lombok.NonNull;
import org.dromara.hutool.core.data.id.IdUtil;
import org.dromara.hutool.core.math.NumberUtil;
import org.dromara.hutool.json.JSONUtil;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

/**
 * Test the apis in {@link PermController}. <br>
 * 测试 {@link PermController} 中的接口。<br>
 * java:S2187 要求测试类中必须有 @Test 标注的方法，否则就认为这不是一个测试类。但当前类是被人调用的，其实是测试类。
 *
 * @author Camio1945
 */
@SuppressWarnings("java:S2187")
public class PermControllerTest {

  private static Long createdId = null;
  private static PermPo createParamObj = null;
  private static PermPo createdObj = null;

  public static void testAll() throws Exception {
    create();
    getById();
    update();
    // getAll() 方法在 RoleControllerTest 中已经测试过了
    deleteById();
  }

  /**
   * Test the create perm api. <br>
   * 测试添加权限接口。
   */
  private static void create() throws Exception {
    createParamObj = new PermPo();
    createParamObj
        .setType(PermTypeEnum.E.name())
        .setPid(0L)
        .setName(IdUtil.nanoId(10))
        .setUri("/tempTest")
        .setSort(10);
    RequestBuilder req = initReq().post("/sys/perm/create").contentJson(createParamObj).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String createdIdStr = res.getResponse().getContentAsString();
    assertNotNull(createdIdStr);
    assertTrue(NumberUtil.isLong(createdIdStr));
    createdId = Long.parseLong(createdIdStr);
  }

  private static void getById() throws Exception {
    createdObj = getById(createdId);
    assertNotNull(createdObj);
    assertEquals(createdObj.getId(), createdId);
    assertEquals(createParamObj.getType(), createdObj.getType());
    assertEquals(createParamObj.getPid(), createdObj.getPid());
    assertEquals(createParamObj.getName(), createdObj.getName());
    assertEquals(createParamObj.getUri(), createdObj.getUri());
    assertEquals(createParamObj.getSort(), createdObj.getSort());
  }

  private static void update() throws Exception {
    PermPo updateParamObj = new PermPo();
    updateParamObj.setId(createdId);
    updateParamObj .setType(PermTypeEnum.E.name())
                   .setPid(4L)
                   .setName(IdUtil.nanoId(10))
                   .setUri("/tempTestNew")
                   .setSort(11);
    RequestBuilder req = initReq().put("/sys/perm/update").contentJson(updateParamObj).build();
    mockMvc.perform(req).andExpect(status().isOk());
    PermPo updatedObj = getById(updateParamObj.getId());
    assertNotNull(updatedObj);
    assertEquals(updatedObj.getId(), createdId);
    assertEquals(updateParamObj.getType(), updatedObj.getType());
    assertEquals(updateParamObj.getPid(), updatedObj.getPid());
    assertEquals(updateParamObj.getName(), updatedObj.getName());
    assertEquals(updateParamObj.getUri(), updatedObj.getUri());
    assertEquals(updateParamObj.getSort(), updatedObj.getSort());
  }

  private static void deleteById() throws Exception {
    PermPo permPo = new PermPo();
    permPo.setId(createdId);
    RequestBuilder req = initReq().delete("/sys/perm/delete").contentJson(permPo).build();
    mockMvc.perform(req).andExpect(status().isOk());
    req = initReq().get("/sys/perm/get/" + createdId).build();
    mockMvc.perform(req).andExpect(status().isNotFound());
  }

  private static PermPo getById(@NonNull Long id) throws Exception {
    RequestBuilder req = initReq().get("/sys/perm/get/" + id).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    return JSONUtil.toBean(resJsonStr, PermPo.class);
  }
}
