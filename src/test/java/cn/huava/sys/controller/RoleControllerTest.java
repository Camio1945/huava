package cn.huava.sys.controller;

import static cn.huava.common.constant.CommonConstant.ADMIN_ROLE_ID;
import static cn.huava.common.constant.TestConstant.ADMIN_ROLE_NAME;
import static cn.huava.common.util.ApiTestUtil.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cn.huava.common.WithSpringBootTestAnnotation;
import cn.huava.common.pojo.dto.PageDto;
import cn.huava.common.util.ApiTestUtil;
import cn.huava.sys.pojo.dto.PermDto;
import cn.huava.sys.pojo.po.RolePo;
import cn.huava.sys.pojo.qo.SetPermQo;
import cn.hutool.v7.core.collection.CollUtil;
import cn.hutool.v7.core.data.id.IdUtil;
import cn.hutool.v7.core.math.NumberUtil;
import cn.hutool.v7.core.reflect.TypeReference;
import cn.hutool.v7.json.JSONUtil;
import java.util.*;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test the apis in {@link RoleController}. <br>
 *
 * @author Camio1945
 */
@Slf4j
@AutoConfigureMockMvc
@Rollback
@Transactional
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class RoleControllerTest extends WithSpringBootTestAnnotation {
  private static final String DESCRIPTION = "测试角色";

  @Autowired MockMvc mockMvcAutowired;

  @AfterAll
  @SneakyThrows
  static void afterAll() {
    logout();
  }

  @BeforeEach
  @SneakyThrows
  void beforeEach() {
    if (ApiTestUtil.mockMvc == null) {
      ApiTestUtil.mockMvc = mockMvcAutowired;
      loginByAdmin();
    }
  }

  @Test
  @SneakyThrows
  void should_create_role() {
    RolePo createParamObj = createTestRole();
    assertThat(createParamObj.getId()).isNotNull();
  }

  @SneakyThrows
  private static RolePo createTestRole() {
    RolePo createParamObj = new RolePo();
    createParamObj.setName(IdUtil.nanoId(10)).setSort(10).setDescription(DESCRIPTION);
    RequestBuilder req = initReq().post("/sys/role/create").contentJson(createParamObj).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String createdIdStr = res.getResponse().getContentAsString();
    assertThat(createdIdStr).isNotBlank();
    assertThat(NumberUtil.isLong(createdIdStr)).isTrue();
    createParamObj.setId(Long.parseLong(createdIdStr));
    return createParamObj;
  }

  @Test
  @SneakyThrows
  void should_not_create_role() {
    RequestBuilder req = initReq().post("/sys/role/create").contentJson(new RolePo()).build();
    mockMvc.perform(req).andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void should_get_role_by_id() {
    RolePo createdObj = getOrCreateTestRole();
    RolePo gotById = getById(createdObj.getId());
    assertThat(gotById).isNotNull();
    assertThat(gotById.getId()).isEqualTo(createdObj.getId());
    assertThat(gotById.getName()).isEqualTo(createdObj.getName());
    assertThat(gotById.getDescription()).isEqualTo(createdObj.getDescription());
  }

  @SneakyThrows
  private static RolePo getOrCreateTestRole() {
    RequestBuilder req =
        initReq().get("/sys/role/page?current=1&size=1&description=" + DESCRIPTION).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    TypeReference<PageDto<RolePo>> type = new TypeReference<>() {};
    PageDto<RolePo> pageDto = JSONUtil.toBean(resJsonStr, type);
    if (pageDto.getCount() > 0) {
      RolePo first = pageDto.getList().getFirst();
      assertEquals(DESCRIPTION, first.getDescription());
      return first;
    }
    return createTestRole();
  }

  @SneakyThrows
  private RolePo getById(@NonNull Long id) {
    RequestBuilder req = initReq().get("/sys/role/get/" + id).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    return JSONUtil.toBean(resJsonStr, RolePo.class);
  }

  @Test
  @SneakyThrows
  void should_query_role_page() {
    RequestBuilder req =
        initReq().get("/sys/role/page?current=1&size=1&name=" + ADMIN_ROLE_NAME).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    TypeReference<PageDto<RolePo>> type = new TypeReference<>() {};
    PageDto<RolePo> pageDto = JSONUtil.toBean(resJsonStr, type);
    assertEquals(1, pageDto.getCount());
    assertEquals(ADMIN_ROLE_ID, pageDto.getList().getFirst().getId());
  }

  @Test
  @SneakyThrows
  void should_role_name_exists() {
    // 当只传入角色名时，相当于查询 name = '传入的角色名'，因此应该返回 true
    RequestBuilder req = initReq().get("/sys/role/isNameExists?name=" + ADMIN_ROLE_NAME).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    assertEquals("true", resJsonStr);
  }

  @Test
  @SneakyThrows
  void should_role_name_not_exists() {
    // 当传入 id 和 角色名 时，相当于查询 name = '传入的角色名' AND id != '传入的 id'，因此应该返回 false
    String url = "/sys/role/isNameExists?id=" + ADMIN_ROLE_ID + "&name=" + ADMIN_ROLE_NAME;
    RequestBuilder req = initReq().get(url).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    assertEquals("false", resJsonStr);
  }

  @Test
  @SneakyThrows
  void should_update_role() {
    RolePo createdObj = getOrCreateTestRole();
    RolePo updateParamObj = new RolePo();
    updateParamObj.setId(createdObj.getId());
    updateParamObj.setName(IdUtil.nanoId(10)).setSort(11).setDescription("测试角色2");
    RequestBuilder req = initReq().put("/sys/role/update").contentJson(updateParamObj).build();
    mockMvc.perform(req).andExpect(status().isOk());
    RolePo updatedObj = getById(updateParamObj.getId());
    assertNotNull(updatedObj);
    assertEquals(createdObj.getId(), updatedObj.getId());
    assertEquals(updateParamObj.getName(), updatedObj.getName());
    assertEquals(updateParamObj.getSort(), updatedObj.getSort());
    assertEquals(updateParamObj.getDescription(), updatedObj.getDescription());
  }

  @Test
  @SneakyThrows
  void should_set_role_permissions() {
    RolePo createdObj = getOrCreateTestRole();
    List<Long> permIdsOfAll = getAllPermIds();
    SetPermQo setPermQo = new SetPermQo();
    Long roleId = createdObj.getId();
    setPermQo.setRoleId(roleId);
    setPermQo.setPermIds(permIdsOfAll);
    RequestBuilder req = initReq().post("/sys/role/setPerm").contentJson(setPermQo).build();
    mockMvc.perform(req).andExpect(status().isOk());
    List<Long> permIdsOfRole = getPermIdsByRoleId(roleId);
    assertFalse(permIdsOfRole.isEmpty());
    assertThat(CollUtil.equals(permIdsOfRole, permIdsOfAll, true)).isTrue();
  }

  @Test
  @SneakyThrows
  void should_clear_role_permissions() {
    RolePo createdObj = getOrCreateTestRole();
    SetPermQo setPermQo = new SetPermQo();
    setPermQo.setRoleId(createdObj.getId());
    RequestBuilder req = initReq().post("/sys/role/setPerm").contentJson(setPermQo).build();
    mockMvc.perform(req).andExpect(status().isOk());

    List<Long> permIds = getPermIdsByRoleId(createdObj.getId());
    assertTrue(permIds.isEmpty());
  }

  private static List<Long> getPermIdsByRoleId(Long roleId) throws Exception {
    RequestBuilder req = initReq().get("/sys/role/getPerm/" + roleId).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    TypeReference<List<Long>> type = new TypeReference<>() {};
    return JSONUtil.toBean(resJsonStr, type);
  }

  @SneakyThrows
  private List<Long> getAllPermIds() {
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

  private void appendPermIds(@NonNull List<Long> permIds, PermDto permDto) {
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

  @Test
  @SneakyThrows
  void should_delete_role_by_id() {
    RolePo createdObj = getOrCreateTestRole();
    RolePo rolePo = new RolePo();
    rolePo.setId(createdObj.getId());
    RequestBuilder req = initReq().delete("/sys/role/delete").contentJson(rolePo).build();
    mockMvc.perform(req).andExpect(status().isOk());
    req = initReq().get("/sys/role/get/" + createdObj.getId()).build();
    mockMvc.perform(req).andExpect(status().isNotFound());
  }
}
