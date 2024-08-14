package cn.huava.common.config;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;

// @Configuration
public class JacksonConfig {

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.addMixIn(
        OAuth2ClientAuthenticationToken.class, OAuth2ClientAuthenticationTokenMixin.class);
    // configureObjectMapper(objectMapper);
    return objectMapper;
  }

  private void configureObjectMapper(ObjectMapper objectMapper) {
    // Register custom Mixins
    SimpleModule module = new SimpleModule();
    module.setMixInAnnotation(
        OAuth2ClientAuthenticationToken.class, OAuth2ClientAuthenticationTokenMixin.class);
    objectMapper.registerModule(module);

    // Uncomment if you need default typing (be cautious with this)
    // objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT,
    // JsonTypeInfo.As.PROPERTY);
  }

  // Define Mixins here or in a separate file
  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@class")
  @JsonSubTypes({
    @JsonSubTypes.Type(
        value = OAuth2ClientAuthenticationToken.class,
        name = "OAuth2ClientAuthenticationToken")
  })
  abstract class OAuth2ClientAuthenticationTokenMixin {
    // Mixin class to allow deserialization
    @JsonCreator
    OAuth2ClientAuthenticationTokenMixin() {}
  }
}
