package cn.huava.common.util;

import static cn.huava.common.constant.CommonConstant.ENV_PROD;
import static cn.huava.common.constant.CommonConstant.ENV_PRODUCTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import cn.huava.common.WithSpringBootTestAnnotation;
import cn.hutool.v7.core.data.id.IdUtil;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Redis 工具类测试 */
class RedisUtilTest extends WithSpringBootTestAnnotation {

  @Test
  void should_has_key() {
    String key = IdUtil.nanoId(10);
    assertFalse(RedisUtil.hasKey(key));
    String value = "tempValue";
    RedisUtil.set(key, value);
    assertTrue(RedisUtil.hasKey(key));
    String redisValue = RedisUtil.get(key);
    assertEquals(value, redisValue);
    RedisUtil.delete(key);
  }

  @Test
  void should_map_contains_key() {
    String mapName = "testMap";
    String key = "key1";
    String value = "value1";
    RedisUtil.putMapValue(mapName, key, value);
    String redisValue = RedisUtil.getMapValue(mapName, key);
    assertEquals(value, redisValue);
    Set<String> keys = RedisUtil.getMapKeys(mapName);
    assertEquals(1, keys.size());
    assertTrue(keys.contains(key));
  }

  @Test
  void should_get_hit_ratio_percentage() {
    RedisUtil.getHitRatioPercentage();
    String key = "hello";
    String value = "world";
    RedisUtil.set(key, value, 10);
    assertThat((String) RedisUtil.get(key)).isEqualTo(value);
    assertThat(RedisUtil.getHitRatioPercentage()).isGreaterThan(0);
  }

  @Test
  void should_allow_flush_in_non_production_env() {
    // This test assumes that the current environment is not prod or production
    // If the current profile is not prod or production, this should not throw an exception
    assertDoesNotThrow(RedisUtil::flushNonProductionDb);
  }

  @Test
  void should_not_allow_flush_in_prod_env() {
    // Mock the environment to simulate prod environment
    System.setProperty("spring.profiles.active", ENV_PROD);

    try {
      assertThrows(IllegalArgumentException.class, RedisUtil::flushNonProductionDb);
    } finally {
      // Clean up the property after test
      System.clearProperty("spring.profiles.active");
    }
  }

  @Test
  void should_not_allow_flush_in_production_env() {
    // Mock the environment to simulate production environment
    System.setProperty("spring.profiles.active", ENV_PRODUCTION);

    try {
      assertThrows(IllegalArgumentException.class, RedisUtil::flushNonProductionDb);
    } finally {
      // Clean up the property after test
      System.clearProperty("spring.profiles.active");
    }
  }
}
