package cn.huava;

import cn.huava.common.WithSpringBootTestAnnotation;
import cn.huava.common.util.RedisUtil;
import cn.huava.common.util.RedisUtilTest;
import cn.huava.sys.controller.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.*;

/**
 * Test the apis that do not require login (not all of them). Such apis are not many, so we just
 * write them in one class.<br>
 * 测试不需要登录的接口（只测试了一部分）。这样的接口不是很多，因此直接写到一个类里面。
 *
 * @author Camio1945
 */
@Slf4j
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class MainTest extends WithSpringBootTestAnnotation {

  @Autowired MockMvc mockMvc;

  @Test
  @Order(1) // has to be run first, because it initializes the mockMvc, flushes redis db
  void tempTestControllerTest() throws Exception {
    ApiTestUtil.mockMvc = mockMvc;
    RedisUtil.flushNonProductionDb();
    TempTestControllerTest.testAll();
  }

  @Test
  @Order(2) // has to be run second, because it initializes captcha which is used by login api
  void captchaControllerTest() throws Exception {
    CaptchaControllerTest.testAll();
  }

  @Test
  @Order(3) // has to be run third, because it logs in the user
  void userControllerTest() throws Exception {
    UserControllerTest.testAllExceptLogout();
  }

  @Test
  @Order(4)
  void utilTest() {
    RedisUtilTest.testAll();
  }

  @Test
  @Order(5)
  void attachmentTest() throws Exception {
    AttachmentTest.testAll();
  }

  @Test
  @Order(5)
  void roleControllerTest() throws Exception {
    RoleControllerTest.testAll();
  }

  @Test
  @Order(6)
  void permControllerTest() throws Exception {
    PermControllerTest.testAll();
  }

  @Test
  @Order(Integer.MAX_VALUE) // has to be run last, because it logs out the user
  void logout() throws Exception {
    AuthTest.testAll();
  }
}
