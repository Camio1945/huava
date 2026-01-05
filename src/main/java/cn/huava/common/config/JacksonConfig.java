package cn.huava.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * 解决前后端交互时 Long 类型精度丢失的问题 (Jackson 3.x 版本)
 *
 * @author Camio1945
 */
@Configuration
public class JacksonConfig {

  @Bean
  public ObjectMapper jacksonObjectMapper() {
    // Jackson 3 推荐使用 JsonMapper.builder() 来构建 ObjectMapper
    return JsonMapper.builder()
        // 注册 Module
        .addModule(
            new SimpleModule()
                // 处理封装类型 long
                .addSerializer(Long.class, ToStringSerializer.instance)
                // 同时也处理基本类型 long
                .addSerializer(Long.TYPE, ToStringSerializer.instance))
        .build();
  }
}
