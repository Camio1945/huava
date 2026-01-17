package cn.huava.common.util;

import static cn.huava.common.constant.CommonConstant.*;
import static cn.huava.common.constant.TestConstant.ADMIN_PASSWORD;
import static cn.huava.common.constant.TestConstant.ADMIN_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cn.huava.common.pojo.ReqBuilder;
import cn.huava.sys.pojo.dto.UserJwtDto;
import cn.huava.sys.pojo.qo.LoginQo;
import cn.hutool.v7.json.JSONUtil;
import lombok.SneakyThrows;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

/**
 * Store mockMvc, session, access token, refresh token. <br>
 * 存储 mockMvc、session、access token、refresh token。
 *
 * @author Camio1945
 */
public class ApiTestUtil {

  public static MockMvc mockMvc;

  public static MockHttpSession session = new MockHttpSession();

  public static String accessToken;

  public static String refreshToken;

  @SneakyThrows
  public static void loginByAdmin() {
    // 一次测试只登录一次
    if (accessToken != null) {
      return;
    }
    UserJwtDto userJwtDto = loginByUsernameAndPassword(ADMIN_USERNAME, ADMIN_PASSWORD);
    assertFalse(userJwtDto.getRefreshToken().isEmpty());
    accessToken = userJwtDto.getAccessToken();
    refreshToken = userJwtDto.getRefreshToken();
  }

  public static UserJwtDto loginByUsernameAndPassword(String username, String password)
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

  public static ReqBuilder initReq() {
    return new ReqBuilder();
  }

  @SneakyThrows
  public static void logout() {
    RequestBuilder req =
        initReq().post("/sys/user/logout").contentTypeText().content(refreshToken).build();
    mockMvc.perform(req).andExpect(status().isOk());
    req = initReq().post("/sys/user/refreshToken").contentTypeText().content(refreshToken).build();
    mockMvc.perform(req).andExpect(status().isBadRequest());
    mockMvc = null;
    accessToken = null;
    refreshToken = null;
  }
}
