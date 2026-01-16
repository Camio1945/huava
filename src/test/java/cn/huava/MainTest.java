package cn.huava;

import cn.huava.common.WithSpringBootTestAnnotation;
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

  @Test
  @Order(4)
  void utilTest() {
    RedisUtilTest.testAll();
  }

  /** 不重要的测试，一般是为了提升覆盖率而增加的。 */
  @Test
  @Order(Integer.MAX_VALUE - 1)
  void notImportantTest() throws Exception {
    // Removed call to old testAll method - now covered by individual unit tests
  }
}
