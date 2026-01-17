package cn.huava.sys.cache;

import cn.huava.common.WithSpringBootTestAnnotation;
import cn.huava.common.util.RedisUtil;
import cn.hutool.v7.extra.spring.SpringUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static cn.huava.common.constant.CommonConstant.ADMIN_ROLE_ID;
import static cn.huava.sys.cache.RoleCache.URIS_CACHE_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;

class RoleCacheTest extends WithSpringBootTestAnnotation {
  @Autowired
  private RoleCache roleCache;

  @Test
  void should_get_perm_uris_by_role_id() {
    Set<String> permUris = roleCache.getPermUrisByRoleId(ADMIN_ROLE_ID);
    assertThat(permUris).isNotNull();
    assertThat(RedisUtil.hasKey(URIS_CACHE_PREFIX + "::" + ADMIN_ROLE_ID)).isTrue();
  }
}
