package cn.huava.sys.controller;

import static cn.huava.common.constant.CommonConstant.CAPTCHA_CODE_SESSION_KEY;
import static cn.huava.sys.controller.ApiTestUtil.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cn.huava.common.WithSpringBootTestAnnotation;
import cn.huava.common.pojo.dto.PageDto;
import cn.huava.common.util.Fn;
import cn.huava.common.util.RedisUtil;
import cn.huava.sys.enumeration.UserGenderEnum;
import cn.huava.sys.pojo.dto.*;
import cn.huava.sys.pojo.po.UserExtPo;
import cn.huava.sys.pojo.qo.LoginQo;
import cn.huava.sys.pojo.qo.UpdatePasswordQo;
import cn.hutool.v7.core.data.id.IdUtil;
import cn.hutool.v7.core.math.NumberUtil;
import cn.hutool.v7.core.reflect.TypeReference;
import cn.hutool.v7.json.JSONUtil;
import java.util.*;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

/**
 * Test the apis in {@link UserController}.
 *
 * @author Camio1945
 */
@Slf4j
@AutoConfigureMockMvc
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class UserControllerTest extends WithSpringBootTestAnnotation {
  private static final String USERNAME = "admin";
  private static final String PASSWORD = "123456";
  private static Long createdId = null;
  private static UserExtPo createParamObj = null;
  private static UserExtPo createdObj = null;
  @Autowired MockMvc mockMvcAutowired;

  @Test
  @SneakyThrows
  void mySelf() {
    RequestBuilder req = initReq().get("/sys/user/mySelf").build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    UserInfoDto userInfoDto = JSONUtil.toBean(resJsonStr, UserInfoDto.class);
    assertNotNull(userInfoDto);
    assertEquals(USERNAME, userInfoDto.getUsername());
    assertFalse(userInfoDto.getMenu().isEmpty());
  }

  /**
   * Test the create user api. <br>
   * 测试添加用户接口。
   */
  @Test
  @SneakyThrows
  void create() {
    createParamObj = new UserExtPo();
    createParamObj
        .setUsername(IdUtil.nanoId(10))
        .setPassword("12345678")
        .setRealName("测试用户")
        .setPhoneNumber("18510336674")
        .setGender(UserGenderEnum.U.name())
        .setIsEnabled(true);
    createParamObj.setRoleIds(List.of(1L));
    RequestBuilder req = initReq().post("/sys/user/create").contentJson(createParamObj).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String createdIdStr = res.getResponse().getContentAsString();
    assertNotNull(createdIdStr);
    assertTrue(NumberUtil.isLong(createdIdStr));
    createdId = Long.parseLong(createdIdStr);
  }

  @Test
  @SneakyThrows
  void getById() {
    createdObj = getById(createdId);
    assertNotNull(createdObj);
    assertEquals(createdObj.getId(), createdId);
    assertEquals(createParamObj.getUsername(), createdObj.getUsername());
    assertEquals(createParamObj.getRealName(), createdObj.getRealName());
    assertEquals(createParamObj.getPhoneNumber(), createdObj.getPhoneNumber());
    assertEquals(createParamObj.getGender(), createdObj.getGender());
    assertEquals(createParamObj.getIsEnabled(), createdObj.getIsEnabled());
  }

  @Test
  @SneakyThrows
  void page() {
    String username = createdObj.getUsername();
    RequestBuilder req =
        initReq().get("/sys/user/page?current=1&size=1&username=" + username).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    TypeReference<PageDto<UserDto>> type = new TypeReference<>() {};
    PageDto<UserDto> pageDto = JSONUtil.toBean(resJsonStr, type);
    assertEquals(1, pageDto.getCount());
    assertEquals(createdId, pageDto.getList().getFirst().getId());
  }

  @Test
  @SneakyThrows
  void isUsernameExists() {
    // 当传入 id 和 用户名 时，相当于查询 username = '传入的用户名' AND id != '传入的 id'，因此应该返回 false
    String username = createdObj.getUsername();
    String url = "/sys/user/isUsernameExists?id=" + createdId + "&username=" + username;
    RequestBuilder req = initReq().get(url).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    assertEquals("false", resJsonStr);
    // 当只传入用户名时，相当于查询 username = '传入的用户名'，因此应该返回 true
    req = initReq().get("/sys/user/isUsernameExists?username=" + username).build();
    res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    resJsonStr = res.getResponse().getContentAsString();
    assertEquals("true", resJsonStr);
  }

  /** 使用新创建的用户登录 */
  @Test
  @SneakyThrows
  void loginAndMySelfByCreatedUser() {
    RequestBuilder req = buildLoginReq(createParamObj.getUsername(), createParamObj.getPassword());
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    UserJwtDto userJwtDto = JSONUtil.toBean(resJsonStr, UserJwtDto.class);
    assertFalse(userJwtDto.getAccessToken().isEmpty());
    assertFalse(userJwtDto.getRefreshToken().isEmpty());
    String preservedAccessToken = accessToken;
    accessToken = userJwtDto.getAccessToken();

    req = initReq().get("/sys/user/mySelf").build();
    res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    resJsonStr = res.getResponse().getContentAsString();
    UserInfoDto userInfoDto = JSONUtil.toBean(resJsonStr, UserInfoDto.class);
    assertNotNull(userInfoDto);
    assertEquals(createParamObj.getUsername(), userInfoDto.getUsername());
    assertFalse(userInfoDto.getMenu().isEmpty());
    accessToken = preservedAccessToken;
  }

  void update() {
    // 修改用户，同时修改密码
    updateWithNewPassword();
    // 修改用户，但是不修改密码，以测试不同的代码分支
    updateWithOldPassword();
  }

  @Test
  @SneakyThrows
  void updatePassword() {
    UpdatePasswordQo updatePasswordQo = new UpdatePasswordQo();
    updatePasswordQo.setOldPassword(PASSWORD);
    String newPassword = PASSWORD + "new";
    updatePasswordQo.setNewPassword(newPassword);
    RequestBuilder req =
        initReq().patch("/sys/user/updatePassword").contentJson(updatePasswordQo).build();
    mockMvc.perform(req).andExpect(status().isOk());

    // 用旧密码不能登录
    req = buildLoginReq(USERNAME, PASSWORD);
    mockMvc.perform(req).andExpect(status().isBadRequest());

    // 用新密码可以登录
    req = buildLoginReq(USERNAME, newPassword);
    mockMvc.perform(req).andExpect(status().isOk());

    // 把密码再改回来
    updatePasswordQo = new UpdatePasswordQo();
    updatePasswordQo.setOldPassword(newPassword);
    updatePasswordQo.setNewPassword(PASSWORD);
    req = initReq().patch("/sys/user/updatePassword").contentJson(updatePasswordQo).build();
    mockMvc.perform(req).andExpect(status().isOk());
  }

  @Test
  @SneakyThrows
  void deleteById() {
    UserExtPo userExtPo = new UserExtPo();
    userExtPo.setId(createdId);
    RequestBuilder req = initReq().delete("/sys/user/delete").contentJson(userExtPo).build();
    mockMvc.perform(req).andExpect(status().isOk());
    req = initReq().get("/sys/user/get/" + createdId).build();
    mockMvc.perform(req).andExpect(status().isNotFound());
  }

  @Test
  @SneakyThrows
  void refreshToken() {
    RequestBuilder req =
        initReq().post("/sys/user/refreshToken").contentTypeText().content(refreshToken).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    accessToken = res.getResponse().getContentAsString();
    assertTrue(Fn.isNotBlank(accessToken));
  }

  @SneakyThrows
  UserExtPo getById(@NonNull Long id) {
    RequestBuilder req = initReq().get("/sys/user/get/" + id).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    return JSONUtil.toBean(resJsonStr, UserExtPo.class);
  }

  RequestBuilder buildLoginReq(@NonNull String username, @NonNull String password) {
    LoginQo loginQo = new LoginQo();
    loginQo.setUsername(username);
    loginQo.setPassword(password);
    // 由于第一次登录以后已经把 session 中的验证码清空了，因此这一次不传验证码了
    loginQo.setIsCaptchaDisabledForTesting(true);
    return initReq()
        .post("/sys/user/login")
        // 这里需要 new 一个新的 session，以免跟之前的 session 搞混淆了
        .session(new MockHttpSession())
        .contentJson(loginQo)
        .build();
  }

  @Test
  @SneakyThrows
  void updateWithNewPassword() {
    UserExtPo updateParamObj = new UserExtPo();
    updateParamObj.setId(createdId);
    updateParamObj
        .setUsername(IdUtil.nanoId(10))
        .setPassword("123456789")
        .setRealName("测试用户2")
        .setPhoneNumber("18510336677")
        .setGender(UserGenderEnum.M.name())
        .setIsEnabled(false);
    updateParamObj.setRoleIds(List.of(1L));
    RequestBuilder req = initReq().put("/sys/user/update").contentJson(updateParamObj).build();
    mockMvc.perform(req).andExpect(status().isOk());
    UserExtPo updatedObj = getById(updateParamObj.getId());
    assertNotNull(updatedObj);
    assertEquals(updatedObj.getId(), createdId);
    assertEquals(updateParamObj.getUsername(), updatedObj.getUsername());
    assertEquals(updateParamObj.getRealName(), updatedObj.getRealName());
    assertEquals(updateParamObj.getPhoneNumber(), updatedObj.getPhoneNumber());
    assertEquals(updateParamObj.getGender(), updatedObj.getGender());
    assertEquals(updateParamObj.getIsEnabled(), updatedObj.getIsEnabled());
  }

  @Test
  @SneakyThrows
  void updateWithOldPassword() {
    UserExtPo updateParamObj = new UserExtPo();
    updateParamObj.setId(createdId);
    updateParamObj
        .setUsername(IdUtil.nanoId(10))
        .setRealName("测试用户2")
        .setPhoneNumber("18510336677")
        .setGender(UserGenderEnum.M.name())
        .setIsEnabled(false);
    updateParamObj.setRoleIds(List.of(1L));
    RequestBuilder req = initReq().put("/sys/user/update").contentJson(updateParamObj).build();
    mockMvc.perform(req).andExpect(status().isOk());
    UserExtPo updatedObj = getById(updateParamObj.getId());
    assertNotNull(updatedObj);
    assertEquals(updatedObj.getId(), createdId);
    assertEquals(updateParamObj.getUsername(), updatedObj.getUsername());
    assertEquals(updateParamObj.getRealName(), updatedObj.getRealName());
    assertEquals(updateParamObj.getPhoneNumber(), updatedObj.getPhoneNumber());
    assertEquals(updateParamObj.getGender(), updatedObj.getGender());
    assertEquals(updateParamObj.getIsEnabled(), updatedObj.getIsEnabled());
  }

  @BeforeEach
  void beforeEach() {
    if (ApiTestUtil.mockMvc == null) {
      ApiTestUtil.mockMvc = mockMvcAutowired;
      RedisUtil.flushNonProductionDb();
      login();
    }
  }

  /**
   * Test the login api. <br>
   * 测试登录接口。
   */
  @SneakyThrows
  @Test
  synchronized void login() {
    // 一次测试只登录一次
    if (accessToken != null) {
      return;
    }
    MvcResult res =
        mockMvc.perform(get("/captcha").session(session)).andExpect(status().isOk()).andReturn();
    assertThat(res.getResponse().getContentAsByteArray()).hasSizeGreaterThan(0);
    LoginQo loginQo = new LoginQo();
    loginQo.setUsername(USERNAME);
    loginQo.setPassword(PASSWORD);
    loginQo.setCaptchaCode((String) session.getAttribute(CAPTCHA_CODE_SESSION_KEY));
    loginQo.setIsCaptchaDisabledForTesting(false);
    RequestBuilder req =
        initReq().post("/sys/user/login").needToken(false).contentJson(loginQo).build();
    res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    UserJwtDto userJwtDto = JSONUtil.toBean(resJsonStr, UserJwtDto.class);
    assertFalse(userJwtDto.getRefreshToken().isEmpty());
    accessToken = userJwtDto.getAccessToken();
    refreshToken = userJwtDto.getRefreshToken();
  }
}
