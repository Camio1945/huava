package cn.huava.common.config;

import cn.huava.common.util.RedisUtil;
import java.time.Duration;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.*;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.*;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.lang.Nullable;
import tools.jackson.databind.ObjectMapper;

/**
 * Redis 配置
 *
 * @author Camio1945
 */
@Slf4j
@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisConfig implements CachingConfigurer {
  private final ObjectMapper objectMapper;
  private final RedisConnectionFactory redisConnectionFactory;

  @Value("${spring.cache.redis.time-to-live}")
  private long redisTimeToLive;


  @Bean
  public RedisCacheConfiguration cacheConfiguration() {
    return RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(new RandomOffsetTtlFunction(Duration.ofMinutes(redisTimeToLive)))
        .disableCachingNullValues()
        .serializeValuesWith(
            SerializationPair.fromSerializer(new GenericJacksonJsonRedisSerializer(objectMapper)));
  }

  @Bean
  @Override
  public CacheManager cacheManager() {
    return RedisCacheManager
        .RedisCacheManagerBuilder
        .fromConnectionFactory(redisConnectionFactory)
        .cacheDefaults(cacheConfiguration())
        .build();
  }

  // The following methods are required by CachingConfigurer interface
  @Override
  public CacheResolver cacheResolver() {
    return null;
  }

  @Override
  public KeyGenerator keyGenerator() {
    return null;
  }
}

record RandomOffsetTtlFunction(Duration duration) implements RedisCacheWriter.TtlFunction {

  /** 这个方法在每次生成 ttl 时都会执行，保证了缓存不会同时过期，而会产生随机的偏移，因此规避了缓存雪崩的问题 */
  @Override
  public @NonNull Duration getTimeToLive(@NonNull Object key, @Nullable Object value) {
    return RedisUtil.randomOffsetDuration(duration);
  }
}
