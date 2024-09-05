package cn.huava.common.util;

import static cn.huava.common.constant.CommonConstant.ENV_PROD;
import static cn.huava.common.constant.CommonConstant.ENV_PRODUCTION;

import java.time.Duration;
import java.util.*;
import lombok.NonNull;
import org.dromara.hutool.core.convert.Convert;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.math.NumberUtil;
import org.dromara.hutool.core.util.RandomUtil;
import org.dromara.hutool.extra.spring.SpringUtil;
import org.redisson.api.*;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author Camio1945
 */
public class RedisUtil {
  private static RedissonClient redissonClient;

  private RedisUtil() {}

  // ============================ Common ==============================

  /**
   * Check if the key exists in Redis.
   *
   * @param key Key
   * @return true if exists, false otherwise
   */
  public static boolean hasKey(String key) {
    RBucket<Object> bucket = getRedissonClient().getBucket(key);
    return bucket.isExists();
  }

  /**
   * 清空非生产环境的 Redis 数据库（由于该操作非常危险，因此不允许在生产环境下执行）
   *
   * @return
   */
  public static void flushNonProductionDb() {
    String env = SpringUtil.getProperty("spring.profiles.active");
    Assert.isTrue(!ENV_PROD.equals(env) && !ENV_PRODUCTION.equals(env), "不允许在生产环境下清空 Redis 数据库");
    getRedissonClient().getKeys().flushdb();
  }

  public static double getHitRatioPercentage() {
    RedisConnection connection =
        Objects.requireNonNull(Fn.getBean(StringRedisTemplate.class).getConnectionFactory())
            .getConnection();
    RedisServerCommands redisServerCommands = connection.serverCommands();
    Properties info = redisServerCommands.info();
    assert info != null;
    long keyspaceHits = Convert.toLong(info.getProperty("keyspace_hits"));
    long keyspaceMisses = Convert.toLong(info.getProperty("keyspace_misses"));
    long total = keyspaceHits + keyspaceMisses;
    if (total <= 0) {
      return -1;
    }
    return NumberUtil.div(keyspaceHits * (float) 100, total, 4).doubleValue();
  }

  /**
   * 生成带随机偏移量的 TTL 时间，比如原先设置的是 60 秒，那么实际过期时间将在 [60,66] 秒之间，以解决缓存雪崩问题。
   *
   * @return 秒数
   */
  public static long randomOffsetDurationInSeconds() {
    String minutesStr =
        Fn.getBean(Environment.class).getProperty("spring.cache.redis.time-to-live");
    long minutes = Convert.toLong(minutesStr);
    Duration duration = Duration.ofMinutes(minutes);
    return randomOffsetDuration(duration).getSeconds();
  }

  /**
   * 生成带随机偏移量的 TTL 时间，比如原先设置的是 60 秒，那么实际过期时间将在 [60,66] 秒之间，以解决缓存雪崩问题。
   *
   * @param duration
   * @return
   */
  public static Duration randomOffsetDuration(Duration duration) {
    long seconds = duration.getSeconds();
    // 10% offset
    long offset = RandomUtil.randomLong(0, (seconds / 10) + 1);
    return Duration.ofSeconds(seconds + offset);
  }

  /**
   * Delete a key (or keys) from Redis.
   *
   * @param keys Keys
   */
  public static void delete(@NonNull String... keys) {
    getRedissonClient().getKeys().delete(keys);
  }

  // ============================ String ==============================

  /**
   * Get the value of a key from Redis.
   *
   * @param key Key
   * @return Value associated with the key
   */
  public static <T> T get(String key) {
    RBucket<T> bucket = getRedissonClient().getBucket(key);
    return bucket.get();
  }

  /**
   * Set a key-value pair in Redis.
   *
   * @param key Key
   * @param value Value
   */
  public static void set(String key, Object value) {
    RBucket<Object> bucket = getRedissonClient().getBucket(key);
    bucket.set(value);
  }

  /**
   * Set a key-value pair in Redis with expiration time.
   *
   * @param key Key
   * @param value Value
   * @param ttlInSeconds Time to live (seconds)
   */
  public static void set(String key, Object value, long ttlInSeconds) {
    RBucket<Object> bucket = getRedissonClient().getBucket(key);
    bucket.set(value, Duration.ofSeconds(ttlInSeconds));
  }

  // ============================ Map ==============================

  /**
   * Get a value from a Redis Map by key.
   *
   * @param mapName Map name
   * @param key Key
   * @return Value associated with the key
   */
  public static <T> T getMapValue(String mapName, String key) {
    RMap<String, T> map = getRedissonClient().getMap(mapName);
    return map.get(key);
  }

  /**
   * Put a key-value pair into a Redis Map.
   *
   * @param mapName Map name
   * @param key Key
   * @param value Value
   */
  public static void putMapValue(String mapName, String key, Object value) {
    RMap<String, Object> map = getRedissonClient().getMap(mapName);
    map.put(key, value);
  }

  /**
   * Get all keys from a Redis Map.
   *
   * @param mapName Map name
   * @return Set of keys
   */
  public static Set<String> getMapKeys(String mapName) {
    RMap<String, Object> map = getRedissonClient().getMap(mapName);
    return map.keySet();
  }

  private static RedissonClient getRedissonClient() {
    if (redissonClient == null) {
      redissonClient =
          SingleFlightUtil.execute("redissonClient", () -> Fn.getBean(RedissonClient.class));
    }
    return redissonClient;
  }
}
