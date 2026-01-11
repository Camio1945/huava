package cn.huava;

import cn.huava.common.WithSpringBootTestAnnotation;
import cn.huava.common.graalvm.RuntimeHintsRegistrarConfigTest;
import cn.huava.common.util.ApiTestUtil;
import cn.huava.common.util.RedisUtil;
import cn.huava.common.util.RedisUtilTest;
import cn.huava.sys.controller.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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

  public static void main(String[] args) {
    try {
      Long id = Long.parseLong("2008374986561216512");
      System.out.println(id);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  @Order(1) // 必须在第 1 位执行， 因为这个方法里面初始化了 mockMvc， 清空了 redis 数据库
  void tempTestControllerTest() {
    ApiTestUtil.mockMvc = mockMvc;
    RedisUtil.flushNonProductionDb();
  }

  @Test
  @Order(2) // 必须在第 2 位执行，方法里面初始化了验证码，用于后续的登录
  void captchaControllerTest() throws Exception {
    // CaptchaControllerTest.testAll();
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
