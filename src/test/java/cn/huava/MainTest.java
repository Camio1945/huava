package cn.huava;

import cn.huava.common.WithSpringBootTestAnnotation;
import cn.huava.common.graalvm.RuntimeHintsRegistrarConfigTest;
import cn.huava.common.util.RedisUtil;
import cn.huava.common.util.RedisUtilTest;
import cn.huava.sys.controller.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.*;

/**
 * 主要的测试类
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
  @Order(1) // 必须在第 1 位执行， 因为这个方法里面初始化了 mockMvc， 清空了 redis 数据库
  void tempTestControllerTest() throws Exception {
    ApiTestUtil.mockMvc = mockMvc;
    RedisUtil.flushNonProductionDb();
    TempTestControllerTest.testAll();
  }

  @Test
  @Order(2) // 必须在第 2 位执行，方法里面初始化了验证码，用于后续的登录
  void captchaControllerTest() throws Exception {
    CaptchaControllerTest.testAll();
  }

  @Test
  @Order(3) // 必须在第 3 位执行，方法里面执行了登录操作，后续的方法才有权限执行
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

  /** 不重要的测试，一般是为了提升覆盖率而增加的。 */
  @Test
  @Order(Integer.MAX_VALUE - 1)
  void notImportantTest() throws Exception {
    RuntimeHintsRegistrarConfigTest.testAll();
  }

  @Test
  @Order(Integer.MAX_VALUE) // 必须在最后执行，因为会退出登录
  void logout() throws Exception {
    AuthTest.testAll();
  }
}
