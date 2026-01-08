package cn.huava.sys.controller;

import static cn.huava.common.constant.CommonConstant.*;
import static cn.huava.sys.controller.ApiTestUtil.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cn.huava.common.WithSpringBootTestAnnotation;
import cn.huava.common.pojo.dto.PageDto;
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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test the apis in {@link UserController}.
 *
 * @author Camio1945
 */
@Slf4j
@AutoConfigureMockMvc
@Rollback
@Transactional
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class UserControllerTest extends WithSpringBootTestAnnotation {
  private static final String ADMIN_USERNAME = "admin";
  private static final String ADMIN_PASSWORD = "123456";
  @Autowired MockMvc mockMvcAutowired;

  @AfterAll
  @SneakyThrows
  static void afterAll() {
    RequestBuilder req =
        initReq().post("/sys/user/logout").contentTypeText().content(refreshToken).build();
    mockMvc.perform(req).andExpect(status().isOk());
    req = initReq().post("/sys/user/refreshToken").contentTypeText().content(refreshToken).build();
    mockMvc.perform(req).andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void should_get_info_of_myself() {
    RequestBuilder req = initReq().get("/sys/user/myself").build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    UserInfoDto userInfoDto = JSONUtil.toBean(resJsonStr, UserInfoDto.class);
    assertNotNull(userInfoDto);
    assertEquals(ADMIN_USERNAME, userInfoDto.getUsername());
    assertFalse(userInfoDto.getMenu().isEmpty());
  }

  /**
   * Test the create user api. <br>
   * 测试添加用户接口。
   */
  @Test
  @SneakyThrows
  void should_create_user() {
    UserExtPo createdUser = createUser();
    assertThat(createdUser.getId()).isNotNull();
  }

  @SneakyThrows
  private static @NonNull UserExtPo createUser() {
    UserExtPo createParamObj = new UserExtPo();
    createParamObj
        .setUsername(IdUtil.nanoId(10))
        .setPassword("12345678")
        .setRealName("测试用户")
        .setPhoneNumber("18510336674")
        .setGender(UserGenderEnum.U.name())
        .setIsEnabled(true);
    createParamObj.setRoleIds(List.of(ADMIN_ROLE_ID));
    RequestBuilder req = initReq().post("/sys/user/create").contentJson(createParamObj).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String createdIdStr = res.getResponse().getContentAsString();
    assertThat(createdIdStr).isNotBlank();
    assertThat(NumberUtil.isLong(createdIdStr)).isTrue();
    createParamObj.setId(Long.parseLong(createdIdStr));
    return createParamObj;
  }

  @Test
  @SneakyThrows
  void should_get_admin_user() {
    UserExtPo user = getById(ADMIN_USER_ID);
    assertThat(user).isNotNull();
    assertThat(user.getId()).isEqualTo(ADMIN_USER_ID);
  }

  @SneakyThrows
  UserExtPo getById(@NonNull Long id) {
    RequestBuilder req = initReq().get("/sys/user/get/" + id).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    return JSONUtil.toBean(resJsonStr, UserExtPo.class);
  }

  @Test
  @SneakyThrows
  void should_query_page() {
    RequestBuilder req =
        initReq().get("/sys/user/page?current=1&size=1&username=" + ADMIN_USERNAME).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    TypeReference<PageDto<UserDto>> type = new TypeReference<>() {};
    PageDto<UserDto> pageDto = JSONUtil.toBean(resJsonStr, type);
    assertThat(pageDto.getCount()).isEqualTo(1);
    assertThat(pageDto.getList().getFirst().getId()).isEqualTo(ADMIN_USER_ID);
  }

  @Test
  @SneakyThrows
  void should_username_exists() {
    // 当只传入用户名时，相当于查询 username = '传入的用户名'，因此应该返回 true
    RequestBuilder req =
        initReq().get("/sys/user/isUsernameExists?username=" + ADMIN_USERNAME).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    assertThat(resJsonStr).isEqualTo("true");
  }

  @Test
  @SneakyThrows
  void should_username_not_exists() {
    // 当传入 neId 和 用户名 时，相当于查询 username = '传入的用户名' AND id != '传入的 id'，因此应该返回 false
    String url = "/sys/user/isUsernameExists?neId=" + ADMIN_USER_ID + "&username=" + ADMIN_USERNAME;
    RequestBuilder req = initReq().get(url).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    assertThat(resJsonStr).isEqualTo("false");
  }

  @Test
  @SneakyThrows
  void should_update_with_new_password() {
    UserExtPo updateParamObj = createUser();
    updateParamObj
        .setUsername(IdUtil.nanoId(10))
        .setPassword("123456789")
        .setRealName("测试用户2")
        .setPhoneNumber("18510336677")
        .setGender(UserGenderEnum.M.name());
    updateParamObj.setRoleIds(List.of(ADMIN_ROLE_ID));
    RequestBuilder req = initReq().put("/sys/user/update").contentJson(updateParamObj).build();
    mockMvc.perform(req).andExpect(status().isOk());
    UserExtPo updatedObj = getById(updateParamObj.getId());
    assertThat(updatedObj).isNotNull();
    assertThat(updatedObj.getId()).isEqualTo(updateParamObj.getId());
    assertThat(updatedObj.getUsername()).isEqualTo(updateParamObj.getUsername());
    // 密码应该被加密
    assertThat(updatedObj.getPassword()).isNotEqualTo(updateParamObj.getPassword());
    assertThat(updatedObj.getRealName()).isEqualTo(updateParamObj.getRealName());
    assertThat(updatedObj.getPhoneNumber()).isEqualTo(updateParamObj.getPhoneNumber());
    assertThat(updatedObj.getGender()).isEqualTo(updateParamObj.getGender());
    assertThat(updatedObj.getIsEnabled()).isEqualTo(updateParamObj.getIsEnabled());
    // 可以使用新密码登录
    UserJwtDto userJwtDto =
        loginByUsernameAndPassword(updateParamObj.getUsername(), updateParamObj.getPassword());
    assertThat(userJwtDto).isNotNull();
  }

  private static UserJwtDto loginByUsernameAndPassword(String username, String password)
      throws Exception {
    MvcResult res =
        mockMvc.perform(get("/captcha").session(session)).andExpect(status().isOk()).andReturn();
    assertThat(res.getResponse().getContentAsByteArray()).hasSizeGreaterThan(0);
    LoginQo loginQo = new LoginQo();
    loginQo.setUsername(username);
    loginQo.setPassword(password);
    loginQo.setCaptchaCode((String) session.getAttribute(CAPTCHA_CODE_SESSION_KEY));
    loginQo.setIsCaptchaDisabledForTesting(false);
    RequestBuilder req =
        initReq().post("/sys/user/login").needToken(false).contentJson(loginQo).build();
    res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    return JSONUtil.toBean(resJsonStr, UserJwtDto.class);
  }

  @Test
  @SneakyThrows
  void should_update_with_old_password() {
    UserExtPo updateParamObj = createUser();
    updateParamObj
        .setUsername(IdUtil.nanoId(10))
        .setRealName("测试用户2")
        .setPhoneNumber("18510336677")
        .setGender(UserGenderEnum.M.name());
    updateParamObj.setRoleIds(List.of(1L));
    RequestBuilder req = initReq().put("/sys/user/update").contentJson(updateParamObj).build();
    mockMvc.perform(req).andExpect(status().isOk());
    UserExtPo updatedObj = getById(updateParamObj.getId());
    assertThat(updatedObj).isNotNull();
    assertThat(updatedObj.getId()).isEqualTo(updateParamObj.getId());
    assertThat(updatedObj.getUsername()).isEqualTo(updateParamObj.getUsername());
    assertThat(updatedObj.getRealName()).isEqualTo(updateParamObj.getRealName());
    assertThat(updatedObj.getPhoneNumber()).isEqualTo(updateParamObj.getPhoneNumber());
    assertThat(updatedObj.getGender()).isEqualTo(updateParamObj.getGender());
    assertThat(updatedObj.getIsEnabled()).isEqualTo(updateParamObj.getIsEnabled());
    // 可以使用旧密码登录
    UserJwtDto userJwtDto =
        loginByUsernameAndPassword(updateParamObj.getUsername(), updateParamObj.getPassword());
    assertThat(userJwtDto).isNotNull();
  }

  @Test
  @SneakyThrows
  void should_update_password() {
    UpdatePasswordQo updatePasswordQo = new UpdatePasswordQo();
    updatePasswordQo.setOldPassword(ADMIN_PASSWORD);
    String newPassword = ADMIN_PASSWORD + "new";
    updatePasswordQo.setNewPassword(newPassword);
    RequestBuilder req =
        initReq().patch("/sys/user/updatePassword").contentJson(updatePasswordQo).build();
    mockMvc.perform(req).andExpect(status().isOk());

    // 用旧密码不能登录
    req = buildLoginReq(ADMIN_PASSWORD);
    mockMvc.perform(req).andExpect(status().isBadRequest());

    // 用新密码可以登录
    req = buildLoginReq(newPassword);
    mockMvc.perform(req).andExpect(status().isOk());

    // 把密码再改回来
    updatePasswordQo = new UpdatePasswordQo();
    updatePasswordQo.setOldPassword(newPassword);
    updatePasswordQo.setNewPassword(ADMIN_PASSWORD);
    req = initReq().patch("/sys/user/updatePassword").contentJson(updatePasswordQo).build();
    mockMvc.perform(req).andExpect(status().isOk());
  }

  private static RequestBuilder buildLoginReq(@NonNull String password) {
    LoginQo loginQo = new LoginQo();
    loginQo.setUsername(ADMIN_USERNAME);
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
  void should_delete_by_id() {
    UserExtPo createdUser = createUser();
    UserExtPo userExtPo = new UserExtPo();
    userExtPo.setId(createdUser.getId());
    RequestBuilder req = initReq().delete("/sys/user/delete").contentJson(userExtPo).build();
    mockMvc.perform(req).andExpect(status().isOk());
    req = initReq().get("/sys/user/get/" + createdUser.getId()).build();
    mockMvc.perform(req).andExpect(status().isNotFound());
  }

  @Test
  @SneakyThrows
  void should_refresh_token() {
    accessToken = null;
    refreshToken = null;
    login();
    RequestBuilder req =
        initReq().post("/sys/user/refreshToken").contentTypeText().content(refreshToken).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    accessToken = res.getResponse().getContentAsString();
    assertThat(accessToken).isNotBlank();
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
    UserJwtDto userJwtDto = loginByUsernameAndPassword(ADMIN_USERNAME, ADMIN_PASSWORD);
    assertFalse(userJwtDto.getRefreshToken().isEmpty());
    accessToken = userJwtDto.getAccessToken();
    refreshToken = userJwtDto.getRefreshToken();
  }

  @BeforeEach
  void beforeEach() {
    if (ApiTestUtil.mockMvc == null) {
      ApiTestUtil.mockMvc = mockMvcAutowired;
      RedisUtil.flushNonProductionDb();
      login();
    }
  }
}
