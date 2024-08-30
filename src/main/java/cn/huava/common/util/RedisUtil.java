package cn.huava.common.util;

import java.time.Duration;
import java.util.*;
import lombok.NonNull;
import org.dromara.hutool.core.convert.Convert;
import org.dromara.hutool.core.math.NumberUtil;
import org.dromara.hutool.core.util.RandomUtil;
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

  private static RedissonClient getRedissonClient() {
    if (redissonClient == null) {
      redissonClient =
          SingleFlightUtil.execute("redissonClient", () -> Fn.getBean(RedissonClient.class));
    }
    return redissonClient;
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

  // ============================ String ==============================

  /**
   * Delete a key (or keys) from Redis.
   *
   * @param keys Keys
   */
  public static void delete(@NonNull String... keys) {
    getRedissonClient().getKeys().delete(keys);
  }

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

  // ============================ Map ==============================

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

  // ============================ Set ==============================

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

  /**
   * Add a value to a Redis Set.
   *
   * @param setName Set name
   * @param value Value to add
   */
  public static void addSetValue(String setName, Object value) {
    RSet<Object> set = getRedissonClient().getSet(setName);
    set.add(value);
  }

  // ============================ List ==============================

  /**
   * Check if a value exists in a Redis Set.
   *
   * @param setName Set name
   * @param value Value to check
   * @return true if exists, false otherwise
   */
  public static boolean isSetMember(String setName, Object value) {
    RSet<Object> set = getRedissonClient().getSet(setName);
    return set.contains(value);
  }

  /**
   * Add a value to a Redis List.
   *
   * @param listName List name
   * @param value Value to add
   */
  public static void addListValue(String listName, Object value) {
    RList<Object> list = getRedissonClient().getList(listName);
    list.add(value);
  }

  /**
   * Get a value from a Redis List by index.
   *
   * @param listName List name
   * @param index Index
   * @return Value at the specified index
   */
  public static Object getListValue(String listName, int index) {
    RList<Object> list = getRedissonClient().getList(listName);
    return list.get(index);
  }

  /**
   * Get the size of a Redis List.
   *
   * @param listName List name
   * @return Size of the list
   */
  public static int getListSize(String listName) {
    RList<Object> list = getRedissonClient().getList(listName);
    return list.size();
  }
}
