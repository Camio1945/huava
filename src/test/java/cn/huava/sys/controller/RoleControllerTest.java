package cn.huava.sys.controller;

import static cn.huava.sys.controller.ApiTestUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cn.huava.common.pojo.dto.PageDto;
import cn.huava.sys.pojo.dto.PermDto;
import cn.huava.sys.pojo.po.RolePo;
import cn.huava.sys.pojo.qo.SetPermQo;
import java.util.*;
import lombok.NonNull;
import org.dromara.hutool.core.data.id.IdUtil;
import org.dromara.hutool.core.math.NumberUtil;
import org.dromara.hutool.core.reflect.TypeReference;
import org.dromara.hutool.json.JSONUtil;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

/**
 * Test the apis in {@link RoleController}. <br>
 * 测试 {@link RoleController} 中的接口。<br>
 * java:S2187 要求测试类中必须有 @Test 标注的方法，否则就认为这不是一个测试类。但当前类是被人调用的，其实是测试类。
 *
 * @author Camio1945
 */
@SuppressWarnings("java:S2187")
public class RoleControllerTest {

  private static Long createdId = null;
  private static RolePo createParamObj = null;
  private static RolePo createdObj = null;

  public static void testAll() throws Exception {
    create();
    getById();
    page();
    isNameExists();
    update();
    setPerm();
    getPerm();
    setPermToEmpty();
    deleteById();
  }

  /**
   * Test the create role api. <br>
   * 测试添加角色接口。
   */
  private static void create() throws Exception {
    createParamObj = new RolePo();
    createParamObj.setName(IdUtil.nanoId(10)).setSort(10).setDescription("测试角色");
    RequestBuilder req = initReq().post("/sys/role/create").contentJson(createParamObj).build();
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
    assertEquals(createParamObj.getName(), createdObj.getName());
    assertEquals(createParamObj.getDescription(), createdObj.getDescription());
  }

  private static void page() throws Exception {
    String name = createdObj.getName();
    RequestBuilder req = initReq().get("/sys/role/page?current=1&size=1&name=" + name).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    TypeReference<PageDto<RolePo>> type = new TypeReference<>() {};
    PageDto<RolePo> pageDto = JSONUtil.toBean(resJsonStr, type);
    assertEquals(1, pageDto.getCount());
    assertEquals(createdId, pageDto.getList().getFirst().getId());
  }

  private static void isNameExists() throws Exception {
    // 当传入 id 和 角色名 时，相当于查询 name = '传入的角色名' AND id != '传入的 id'，因此应该返回 false
    String name = createdObj.getName();
    String url = "/sys/role/isNameExists?id=" + createdId + "&name=" + name;
    RequestBuilder req = initReq().get(url).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    assertEquals("false", resJsonStr);
    // 当只传入角色名时，相当于查询 name = '传入的角色名'，因此应该返回 true
    req = initReq().get("/sys/role/isNameExists?name=" + name).build();
    res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    resJsonStr = res.getResponse().getContentAsString();
    assertEquals("true", resJsonStr);
  }

  private static void update() throws Exception {
    RolePo updateParamObj = new RolePo();
    updateParamObj.setId(createdId);
    updateParamObj.setName(IdUtil.nanoId(10)).setSort(11).setDescription("测试角色2");
    RequestBuilder req = initReq().put("/sys/role/update").contentJson(updateParamObj).build();
    mockMvc.perform(req).andExpect(status().isOk());
    RolePo updatedObj = getById(updateParamObj.getId());
    assertNotNull(updatedObj);
    assertEquals(updatedObj.getId(), createdId);
    assertEquals(updateParamObj.getName(), updatedObj.getName());
    assertEquals(updateParamObj.getSort(), updatedObj.getSort());
    assertEquals(updateParamObj.getDescription(), updatedObj.getDescription());
  }

  private static void setPerm() throws Exception {
    List<Long> permIds = getPermIds();
    SetPermQo setPermQo = new SetPermQo();
    setPermQo.setRoleId(createdId);
    setPermQo.setPermIds(permIds);
    RequestBuilder req = initReq().post("/sys/role/setPerm").contentJson(setPermQo).build();
    mockMvc.perform(req).andExpect(status().isOk());
  }

  private static void getPerm() throws Exception {
    RequestBuilder req = initReq().get("/sys/role/getPerm/" + createdId).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    TypeReference<List<Long>> type = new TypeReference<>() {};
    List<Long> permIds = JSONUtil.toBean(resJsonStr, type);
    assertFalse(permIds.isEmpty());
  }

  private static void setPermToEmpty() throws Exception {
    SetPermQo setPermQo = new SetPermQo();
    setPermQo.setRoleId(createdId);
    RequestBuilder req = initReq().post("/sys/role/setPerm").contentJson(setPermQo).build();
    mockMvc.perform(req).andExpect(status().isOk());

    req = initReq().get("/sys/role/getPerm/" + createdId).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    TypeReference<List<Long>> type = new TypeReference<>() {};
    List<Long> permIds = JSONUtil.toBean(resJsonStr, type);
    assertTrue(permIds.isEmpty());
  }

  private static List<Long> getPermIds() throws Exception {
    RequestBuilder req = initReq().get("/sys/perm/getAll").build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    TypeReference<List<PermDto>> type = new TypeReference<>() {};
    String resJsonStr = res.getResponse().getContentAsString();
    List<PermDto> permDtoList = JSONUtil.toBean(resJsonStr, type);
    // 优先获取『用户角色权限』一级菜单及子孙菜单，获取不到的话就取第一个一级菜单及其子孙菜单
    Optional<PermDto> optionalPermDto =
        permDtoList.stream().filter(p -> p.getName().contains("权限")).findAny();
    PermDto permDto = optionalPermDto.orElse(permDtoList.getFirst());
    List<Long> permIds = new ArrayList<>();
    appendPermIds(permIds, permDto);
    return permIds;
  }

  private static void appendPermIds(@NonNull List<Long> permIds, PermDto permDto) {
    if (permDto == null) {
      return;
    }
    permIds.add(permDto.getId());
    List<PermDto> children = permDto.getChildren();
    if (children == null || children.isEmpty()) {
      return;
    }
    children.forEach(child -> appendPermIds(permIds, child));
  }

  private static void deleteById() throws Exception {
    RolePo rolePo = new RolePo();
    rolePo.setId(createdId);
    RequestBuilder req = initReq().delete("/sys/role/delete").contentJson(rolePo).build();
    mockMvc.perform(req).andExpect(status().isOk());
    req = initReq().get("/sys/role/get/" + createdId).build();
    mockMvc.perform(req).andExpect(status().isNotFound());
  }

  private static RolePo getById(@NonNull Long id) throws Exception {
    RequestBuilder req = initReq().get("/sys/role/get/" + id).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    return JSONUtil.toBean(resJsonStr, RolePo.class);
  }
}
