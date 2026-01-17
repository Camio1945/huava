package cn.huava.sys.cache;

import static cn.huava.common.constant.CommonConstant.ADMIN_ROLE_ID;
import static cn.huava.sys.cache.RoleCache.URIS_CACHE_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;

import cn.huava.common.WithSpringBootTestAnnotation;
import cn.huava.common.util.RedisUtil;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RoleCacheTest extends WithSpringBootTestAnnotation {
  @Autowired private RoleCache roleCache;

  @Test
  void should_get_empty_perm_uris_by_admin_role_id() {
    String key = URIS_CACHE_PREFIX + "::" + ADMIN_ROLE_ID;
    roleCache.deleteCache(ADMIN_ROLE_ID);
    Set<String> permUris = roleCache.getPermUrisByRoleId(ADMIN_ROLE_ID);
    assertThat(permUris).isNotNull().isEmpty();
    assertThat(RedisUtil.hasKey(key)).isTrue();
  }
}
