package cn.huava.sys.cache;

import static cn.huava.common.constant.CommonConstant.ADMIN_USER_ID;
import static cn.huava.sys.cache.UserCache.USER_ID_CACHE_PREFIX;
import static cn.huava.sys.cache.UserCache.USER_USERNAME_CACHE_PREFIX;
import static org.assertj.core.api.Assertions.*;

import cn.huava.common.WithSpringBootTestAnnotation;
import cn.huava.common.util.RedisUtil;
import cn.huava.sys.pojo.po.UserExtPo;
import cn.hutool.v7.core.data.id.IdUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for {@link UserCache}
 *
 * @author Camio1945
 */
class UserCacheTest extends WithSpringBootTestAnnotation {

  @Autowired private UserCache userCache;

  @Test
  void should_get_user_by_id() {
    UserExtPo user = userCache.getById(ADMIN_USER_ID);
    assertThat(user.getId()).isEqualTo(ADMIN_USER_ID);
  }

  @Test
  void should_get_id_by_username() {
    Long id = userCache.getIdByUsername("admin");
    assertThat(id).isEqualTo(ADMIN_USER_ID);
  }

  @Test
  void should_get_null_by_non_exist_username() {
    Long id = userCache.getIdByUsername(IdUtil.getSnowflakeNextIdStr());
    assertThat(id).isNull();
  }

  @Test
  void should_delete_keys_after_save_or_update() {
    UserExtPo user = userCache.getById(ADMIN_USER_ID);
    userCache.afterSaveOrUpdate(user);
    assertThat(RedisUtil.hasKey(USER_ID_CACHE_PREFIX + "::" + user.getId())).isFalse();
    assertThat(RedisUtil.hasKey(USER_USERNAME_CACHE_PREFIX + "::" + user.getUsername())).isFalse();
  }

  @Test
  void should_delete_keys_after_delete() {
    UserExtPo user = userCache.getById(ADMIN_USER_ID);
    userCache.afterDelete(user);
    assertThat(RedisUtil.hasKey(USER_ID_CACHE_PREFIX + "::" + user.getId())).isFalse();
    assertThat(RedisUtil.hasKey(USER_USERNAME_CACHE_PREFIX + "::" + user.getUsername())).isFalse();
  }

  @Test
  void should_delete_keys_before_update() {
    UserExtPo user = userCache.getById(ADMIN_USER_ID);
    userCache.beforeUpdate(user);
    assertThat(RedisUtil.hasKey(USER_USERNAME_CACHE_PREFIX + "::" + user.getUsername())).isFalse();
  }
}
