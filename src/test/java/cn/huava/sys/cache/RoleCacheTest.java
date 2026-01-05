package cn.huava.sys.cache;

import cn.hutool.v7.extra.spring.SpringUtil;

public class RoleCacheTest {
  public static void testAll() {
    getPermUrisByRoleId();
  }

  private static void getPermUrisByRoleId() {
    RoleCache roleCache = SpringUtil.getBean(RoleCache.class);
    roleCache.getPermUrisByRoleId(1L);
  }
}
