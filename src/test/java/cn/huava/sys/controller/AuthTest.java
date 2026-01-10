package cn.huava.sys.controller;

import static cn.huava.common.constant.CommonConstant.ADMIN_USER_ID;
import static cn.huava.common.util.ApiTestUtil.*;
import static cn.huava.common.util.ApiTestUtil.refreshToken;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cn.huava.common.util.ApiTestUtil;
import cn.huava.sys.enumeration.UserGenderEnum;
import cn.huava.sys.pojo.dto.UserJwtDto;
import cn.huava.sys.pojo.po.RolePo;
import cn.huava.sys.pojo.po.UserExtPo;
import cn.huava.sys.pojo.qo.LoginQo;
import java.util.*;
import cn.hutool.v7.core.data.id.IdUtil;
import cn.hutool.v7.core.math.NumberUtil;
import cn.hutool.v7.extra.spring.SpringUtil;
import cn.hutool.v7.json.JSONUtil;
import cn.hutool.v7.json.jwt.JWTUtil;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

/**
 * 权限测试 <br>
 * java:S2187 要求测试类中必须有 @Test 标注的方法，否则就认为这不是一个测试类。但当前类是被人调用的，其实是测试类。
 *
 * @author Camio1945
 */
@SuppressWarnings("java:S2187")
public class AuthTest {
  private static Long createdRoleId = null;
  private static UserExtPo createUserParamObj = null;

  public static void testAll() throws Exception {
    createRoleByAdmin();
    createUserByAdmin();
    logoutAdmin();
    loginWithCreatedUser();
    createRoleByCreatedUser();
    tokenExpired();
  }

  /**
   * Test the create role api. <br>
   * 测试添加角色接口。
   */
  private static void createRoleByAdmin() throws Exception {
    RolePo createParamObj = new RolePo();
    createParamObj.setName(IdUtil.nanoId(10)).setSort(10).setDescription("测试角色");
    RequestBuilder req = initReq().post("/sys/role/create").contentJson(createParamObj).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String createdIdStr = res.getResponse().getContentAsString();
    assertNotNull(createdIdStr);
    assertTrue(NumberUtil.isLong(createdIdStr));
    createdRoleId = Long.parseLong(createdIdStr);
  }

  /**
   * Test the create user api. <br>
   * 测试添加用户接口。（使用的是刚刚创建的角色，没有任何权限）
   */
  private static void createUserByAdmin() throws Exception {
    createUserParamObj = new UserExtPo();
    createUserParamObj
        .setUsername(IdUtil.nanoId(10))
        .setPassword("12345678")
        .setRealName("测试用户")
        .setPhoneNumber("18510336674")
        .setGender(UserGenderEnum.U.name())
        .setIsEnabled(true);
    createUserParamObj.setRoleIds(List.of(createdRoleId));
    RequestBuilder req = initReq().post("/sys/user/create").contentJson(createUserParamObj).build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String createdIdStr = res.getResponse().getContentAsString();
    assertNotNull(createdIdStr);
    assertTrue(NumberUtil.isLong(createdIdStr));
  }

  private static void logoutAdmin() throws Exception {
    RequestBuilder req =
        initReq().post("/sys/user/logout").contentTypeText().content(refreshToken).build();
    mockMvc.perform(req).andExpect(status().isOk());

    req = initReq().post("/sys/user/refreshToken").contentTypeText().content(refreshToken).build();
    mockMvc.perform(req).andExpect(status().isBadRequest());
  }

  private static void loginWithCreatedUser() throws Exception {
    LoginQo loginQo = new LoginQo();
    loginQo.setUsername(createUserParamObj.getUsername());
    loginQo.setPassword(createUserParamObj.getPassword());
    // 由于第一次登录以后已经把 session 中的验证码清空了，因此这一次不传验证码了
    loginQo.setIsCaptchaDisabledForTesting(true);
    RequestBuilder req =
        initReq()
            .post("/sys/user/login")
            // 这里需要 new 一个新的 session，以免跟之前的 session 搞混淆了
            .session(new MockHttpSession())
            .contentJson(loginQo)
            .build();
    MvcResult res = mockMvc.perform(req).andExpect(status().isOk()).andReturn();
    String resJsonStr = res.getResponse().getContentAsString();
    UserJwtDto userJwtDto = JSONUtil.toBean(resJsonStr, UserJwtDto.class);
    assertFalse(userJwtDto.getAccessToken().isEmpty());
    assertFalse(userJwtDto.getRefreshToken().isEmpty());
    ApiTestUtil.accessToken = userJwtDto.getAccessToken();
  }

  /**
   * Test the create role api. <br>
   * 测试添加角色接口。
   */
  private static void createRoleByCreatedUser() throws Exception {
    RolePo createParamObj = new RolePo();
    createParamObj.setName(IdUtil.nanoId(10)).setSort(10).setDescription("测试角色");
    RequestBuilder req = initReq().post("/sys/role/create").contentJson(createParamObj).build();
    mockMvc.perform(req).andExpect(status().isForbidden());
  }

  /** token */
  private static void tokenExpired() throws Exception {
    String jwtKeyBase64 = SpringUtil.getProperty("project.jwt_key_base64");
    byte[] jwtKey = Base64.getDecoder().decode(jwtKeyBase64);
    Map<String, Object> payload = HashMap.newHashMap(3);
    payload.put("sub", ADMIN_USER_ID);
    payload.put("iat", System.currentTimeMillis() / 1000);
    // expires now
    payload.put("exp", System.currentTimeMillis() / 1000);
    ApiTestUtil.accessToken = JWTUtil.createToken(payload, jwtKey);
    RequestBuilder req = initReq().post("/sys/user/get/" + ADMIN_USER_ID).build();
    mockMvc.perform(req).andExpect(status().isUnauthorized());
  }
}
