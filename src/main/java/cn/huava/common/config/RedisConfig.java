package cn.huava.common.config;

import cn.huava.common.util.RedisUtil;
import java.time.Duration;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.*;
import org.springframework.data.redis.serializer.*;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.lang.Nullable;

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

  @Value("${spring.cache.redis.time-to-live}")
  private long redisTimeToLive;

  @Bean
  public RedisCacheConfiguration cacheConfiguration() {
    return RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(new RandomOffsetTtlFunction(Duration.ofMinutes(redisTimeToLive)))
        .disableCachingNullValues()
        .serializeValuesWith(
            SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
  }
}

record RandomOffsetTtlFunction(Duration duration) implements RedisCacheWriter.TtlFunction {

  @Override
  public @NonNull Duration getTimeToLive(@NonNull Object key, @Nullable Object value) {
    return RedisUtil.randomOffsetDuration(duration);
  }
}