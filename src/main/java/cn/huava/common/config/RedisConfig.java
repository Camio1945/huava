package cn.huava.common.config;

import cn.huava.common.util.RedisUtil;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.TimeZone;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.*;
import org.springframework.data.redis.serializer.*;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

/**
 * Redis 配置
 *
 * @author Camio1945
 */
@Slf4j
@NullMarked
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
            SerializationPair.fromSerializer(
                new GenericJacksonJsonRedisSerializer(getObjectMapper())));
  }

  private static ObjectMapper getObjectMapper() {
    PolymorphicTypeValidator ptv =
        BasicPolymorphicTypeValidator.builder()
            // Allow all POJOs that extend BasePo
            .allowIfSubType(cn.huava.common.pojo.po.BasePo.class)
            // Trust Dates
            .allowIfBaseType(java.util.Date.class)
            // Trust Collections
            .allowIfSubType(java.util.Collection.class)
            .build();
    return JsonMapper.builder()
        .defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
        .defaultTimeZone(TimeZone.getTimeZone("GMT+8"))
        .activateDefaultTyping(ptv, DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY)
        .build();
  }
}

@NullMarked
record RandomOffsetTtlFunction(Duration duration) implements RedisCacheWriter.TtlFunction {

  /** 这个方法在每次生成 ttl 时都会执行，保证了缓存不会同时过期，而会产生随机的偏移，因此规避了缓存雪崩的问题 */
  @Override
  public Duration getTimeToLive(Object key, @Nullable Object value) {
    return RedisUtil.randomOffsetDuration(duration);
  }
}
