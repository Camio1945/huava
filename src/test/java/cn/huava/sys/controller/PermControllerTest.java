package cn.huava.sys.controller;

import static cn.huava.common.util.ApiTestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cn.huava.common.WithSpringBootTestAnnotation;
import cn.huava.common.util.ApiTestUtil;
import cn.huava.sys.enumeration.PermTypeEnum;
import cn.huava.sys.pojo.dto.PermDto;
import cn.huava.sys.pojo.po.PermPo;
import cn.hutool.v7.core.data.id.IdUtil;
import cn.hutool.v7.core.math.NumberUtil;
import cn.hutool.v7.core.reflect.TypeReference;
import cn.hutool.v7.json.JSONUtil;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.SneakyThrows;
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
 * Test the apis in {@link PermController}. <br>
 *
 * @author Camio1945
 */
@Rollback
@Transactional
@AutoConfigureMockMvc
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class PermControllerTest extends WithSpringBootTestAnnotation {

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
  void should_create_perm() {
    PermPo createParamObj = createTestPerm();
    assertThat(createParamObj.getId()).isNotNull();
  }

  @SneakyThrows
  private static PermPo createTestPerm() {
    PermPo createParamObj = new PermPo();
    createParamObj
        .setType(PermTypeEnum.E.name())
        .setPid(0L)
        .setName(IdUtil.nanoId(10))
        .setUri("/tempTest")
        .setSort(10);
    RequestBuilder req = initReq().post("/sys/perm/create").contentJson(createParamObj).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String createdIdStr = res.getResponse().getContentAsString();
    assertThat(createdIdStr).isNotBlank();
    assertThat(NumberUtil.isLong(createdIdStr)).isTrue();
    createParamObj.setId(Long.parseLong(createdIdStr));
    return createParamObj;
  }

  @Test
  @SneakyThrows
  void should_get_perm_by_id() {
    PermPo createdObj = createTestPerm();
    PermPo gotById = getById(createdObj.getId());
    assertThat(gotById).isNotNull();
    assertThat(gotById.getId()).isEqualTo(createdObj.getId());
    assertThat(createdObj.getType()).isEqualTo(gotById.getType());
    assertThat(createdObj.getPid()).isEqualTo(gotById.getPid());
    assertThat(createdObj.getName()).isEqualTo(gotById.getName());
    assertThat(createdObj.getUri()).isEqualTo(gotById.getUri());
    assertThat(createdObj.getSort()).isEqualTo(gotById.getSort());
  }

  @SneakyThrows
  private PermPo getById(@NonNull Long id) {
    RequestBuilder req = initReq().get("/sys/perm/get/" + id).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    return JSONUtil.toBean(resJsonStr, PermPo.class);
  }

  @Test
  @SneakyThrows
  void should_update_perm() {
    PermPo createdObj = createTestPerm();
    PermPo updateParamObj = new PermPo();
    updateParamObj.setId(createdObj.getId());
    updateParamObj
        .setType(PermTypeEnum.E.name())
        .setPid(4L)
        .setName(IdUtil.nanoId(10))
        .setUri("/tempTestNew")
        .setSort(11);
    RequestBuilder req = initReq().put("/sys/perm/update").contentJson(updateParamObj).build();
    mockMvc.perform(req).andExpect(status().isOk());
    PermPo updatedObj = getById(updateParamObj.getId());
    assertThat(updatedObj).isNotNull();
    assertThat(updatedObj.getId()).isEqualTo(createdObj.getId());
    assertThat(updateParamObj.getType()).isEqualTo(updatedObj.getType());
    assertThat(updateParamObj.getPid()).isEqualTo(updatedObj.getPid());
    assertThat(updateParamObj.getName()).isEqualTo(updatedObj.getName());
    assertThat(updateParamObj.getUri()).isEqualTo(updatedObj.getUri());
    assertThat(updateParamObj.getSort()).isEqualTo(updatedObj.getSort());
  }

  @Test
  @SneakyThrows
  void should_delete_perm_by_id() {
    PermPo createdObj = createTestPerm();
    PermPo permPo = new PermPo();
    permPo.setId(createdObj.getId());
    RequestBuilder req = initReq().delete("/sys/perm/delete").contentJson(permPo).build();
    mockMvc.perform(req).andExpect(status().isOk());
    req = initReq().get("/sys/perm/get/" + createdObj.getId()).build();
    mockMvc.perform(req).andExpect(status().isNotFound());
  }

  @Test
  @SneakyThrows
  void should_get_all_perms() {
    PermPo createdObj = createTestPerm();
    RequestBuilder req = initReq().get("/sys/perm/getAll").build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    TypeReference<List<PermDto>> type = new TypeReference<>() {};
    String resJsonStr = res.getResponse().getContentAsString();
    List<PermDto> permDtos = JSONUtil.toBean(resJsonStr, type);
    assertThat(permDtos).isNotNull();
    Optional<PermDto> any =
        permDtos.stream().filter(permDto -> permDto.getId().equals(createdObj.getId())).findAny();
    assertThat(any).isPresent();
  }
}
