package cn.huava.common.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import org.dromara.hutool.core.data.id.IdUtil;

/** 测试 {@link RedisUtil} 类，由于依赖 Spring 环境，所以需要在 {@link cn.huava.MainTest} 中调用当前类。 */
public class RedisUtilTest {

  public static void testAll() {
    hasKey();
    map();
  }

  private static void hasKey() {
    String key = IdUtil.nanoId(10);
    assertFalse(RedisUtil.hasKey(key));
    String value = "tempValue";
    RedisUtil.set(key, value);
    assertTrue(RedisUtil.hasKey(key));
    String redisValue = RedisUtil.get(key);
    assertEquals(value, redisValue);
    RedisUtil.delete(key);
  }

  private static void map() {
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
}
