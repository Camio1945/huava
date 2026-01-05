package cn.huava.common.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import cn.hutool.v7.core.data.id.IdUtil;
import org.junit.jupiter.api.Test;

/** 测试 {@link Fn} 类，仅测试在 {@link cn.huava.sys.controller.ApiTest} 中涵盖不到的方法。 */
class FnTest {

  @Test
  void exists() {
    assertFalse(Fn.exists(new File(IdUtil.getSnowflakeNextIdStr())));
    assertTrue(Fn.exists(new File(System.getProperty("user.home"))));
  }

  @Test
  void isInJar() {
    assertFalse(Fn.isInJar());
  }
}
