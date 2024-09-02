package cn.huava.sys.controller;

import static cn.huava.sys.controller.ApiTestUtil.mockMvc;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.MvcResult;

/**
 * Test the apis in {@link TempTestController}. <br>
 * 测试 {@link TempTestController} 中的接口。 <br>
 * java:S2187 要求测试类中必须有 @Test 标注的方法，否则就认为这不是一个测试类。但当前类是被人调用的，其实是测试类。
 *
 * @author Camio1945
 */
@SuppressWarnings("java:S2187")
public class TempTestControllerTest {

  public static void testAll() throws Exception {
    hello();
    test();
  }

  /**
   * Temp test: call hello, return world <br>
   * 临时测试：调用 hello 返回 world
   */
  private static void hello() throws Exception {
    mockMvc
        .perform(get("/temp/test/hello"))
        .andExpect(status().isOk())
        .andExpect(content().string("world"));
  }

  /**
   * Temp Test: call test, check if the response is not empty <br>
   * 临时测试：调用 test，检查响应是否不为空。
   */
  private static void test() throws Exception {
    MvcResult res = mockMvc.perform(get("/temp/test/")).andExpect(status().isOk()).andReturn();
    assertTrue(res.getResponse().getContentAsByteArray().length > 0);
  }
}
