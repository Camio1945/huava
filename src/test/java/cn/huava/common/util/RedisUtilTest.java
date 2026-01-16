package cn.huava.common.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import cn.huava.common.WithSpringBootTestAnnotation;
import cn.hutool.v7.core.data.id.IdUtil;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Redis 工具类测试 */
class RedisUtilTest extends WithSpringBootTestAnnotation {

  @Test
  void hasKey() {
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
  void map() {
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
    String key = "hello";
    String value = "world";
    RedisUtil.set(key, value, 10);
    assertThat((String) RedisUtil.get(key)).isEqualTo(value);
    assertThat(RedisUtil.getHitRatioPercentage()).isGreaterThan(0);
  }
}
