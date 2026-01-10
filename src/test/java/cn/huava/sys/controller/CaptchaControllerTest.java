package cn.huava.sys.controller;

import static cn.huava.common.constant.CommonConstant.CAPTCHA_CODE_SESSION_KEY;
import static cn.huava.common.util.ApiTestUtil.mockMvc;
import static cn.huava.common.util.ApiTestUtil.session;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cn.huava.common.controller.CaptchaController;
import cn.huava.common.util.Fn;
import java.io.File;
import java.nio.file.Paths;
import cn.hutool.v7.core.io.file.FileUtil;
import cn.hutool.v7.extra.spring.SpringUtil;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Test the apis in {@link CaptchaController}. <br>
 * 测试 {@link CaptchaController} 中的接口。 <br>
 * java:S2187 要求测试类中必须有 @Test 标注的方法，否则就认为这不是一个测试类。但当前类是被人调用的，其实是测试类。
 *
 * @author Camio1945
 */
@SuppressWarnings("java:S2187")
public class CaptchaControllerTest {

  public static void testAll() throws Exception {
    captchaFromJava();
    captchaFromGraalVM();
  }

  /**
   * Test the captcha api. <br>
   * 测试验证码接口。
   */
  private static void captchaFromJava() throws Exception {
    sendRequest();
  }

  private static void captchaFromGraalVM() throws Exception {
    String key = "org.graalvm.nativeimage.imagecode";
    String originalValue = System.getProperty(key);
    System.setProperty(key, "runtime");
    captchaFromLocal();
    captchaFromOnline();
    if (Fn.isBlank(originalValue)) {
      System.clearProperty(key);
    } else {
      System.setProperty(key, originalValue);
    }
  }

  private static void sendRequest() throws Exception {
    MvcResult res =
        mockMvc.perform(get("/captcha").session(session)).andExpect(status().isOk()).andReturn();
    assertTrue(res.getResponse().getContentAsByteArray().length > 0);
    String captchaCode = (String) session.getAttribute(CAPTCHA_CODE_SESSION_KEY);
    assertTrue(captchaCode != null && captchaCode.length() >= 4);
  }

  /**
   * 先把 project.mxnzp_roll_api.app_id_path 文件重命名，这样就无法从线上获取验证码了，就会走本地分支。 <br>
   * 再把 org.graalvm.nativeimage.imagecode 系统属性设置为 runtime，这样就模拟 GraalVM 环境。 <br>
   * 请求返回以后再把 project.mxnzp_roll_api.app_id_path 文件恢复原名，这样就能在下一个方法里从线上获取验证码了。 <br>
   */
  private static void captchaFromLocal() throws Exception {
    String appIdPath = SpringUtil.getProperty("project.mxnzp_roll_api.app_id_path");
    if (!Paths.get(appIdPath).isAbsolute()) {
      appIdPath = System.getProperty("user.home") + File.separator + appIdPath;
    }
    appIdPath = Fn.cleanPath(appIdPath);
    boolean exists = Fn.exists(appIdPath);
    if (exists) {
      FileUtil.rename(new File(appIdPath), appIdPath + ".bak", false);
    }
    sendRequest();
    if (exists) {
      FileUtil.rename(new File(appIdPath + ".bak"), appIdPath, false);
    }
  }

  /**
   * Test the captcha api (simulate GraalVM environment). <br>
   * 测试验证码接口（模拟 GraalVM 环境）。
   */
  private static void captchaFromOnline() throws Exception {
    sendRequest();
  }
}
