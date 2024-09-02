package cn.huava.common.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import org.junit.jupiter.api.Test;

/** 测试 {@link Fn} 类，仅测试在 {@link cn.huava.sys.controller.ApiTest} 中涵盖不到的方法。 */
class FnTest {

  @Test
  void exists() {
    assertFalse(Fn.exists(new File("")));
    assertTrue(Fn.exists(new File(System.getProperty("user.home"))));
  }
}
