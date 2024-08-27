  
---

# v.0.1.1 - Target

Test refresh token.

# This version does not support GraalVM native image

I cannot solve this problem :

```
Caused by: com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Class org.springframework.security.oauth2.server.authorization.jackson2.UnmodifiableMapDeserializer has no default (no arg) constructor
 at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); line: 1, column: 11]
        at com.fasterxml.jackson.databind.DeserializationContext.reportBadDefinition(DeserializationContext.java:1887) ~[huava:2.17.2]
        at com.fasterxml.jackson.databind.deser.DeserializerCache._createAndCache2(DeserializerCache.java:299) ~[na:na]
        at com.fasterxml.jackson.databind.deser.DeserializerCache._createAndCacheValueDeserializer(DeserializerCache.java:273) ~[na:na]
        at com.fasterxml.jackson.databind.deser.DeserializerCache.findValueDeserializer(DeserializerCache.java:173) ~[na:na]
        at com.fasterxml.jackson.databind.DeserializationContext.findContextualValueDeserializer(DeserializationContext.java:636) ~[huava:2.17.2]
        at com.fasterxml.jackson.databind.jsontype.impl.TypeDeserializerBase._findDeserializer(TypeDeserializerBase.java:203) ~[huava:2.17.2]
        at com.fasterxml.jackson.databind.jsontype.impl.AsPropertyTypeDeserializer._deserializeTypedForId(AsPropertyTypeDeserializer.java:151) ~[huava:2.17.2]
        at com.fasterxml.jackson.databind.jsontype.impl.AsPropertyTypeDeserializer.deserializeTypedFromObject(AsPropertyTypeDeserializer.java:136) ~[huava:2.17.2]
        at com.fasterxml.jackson.databind.deser.std.MapDeserializer.deserializeWithType(MapDeserializer.java:492) ~[na:na]
        at com.fasterxml.jackson.databind.deser.impl.TypeWrappedDeserializer.deserialize(TypeWrappedDeserializer.java:74) ~[na:na]
        at com.fasterxml.jackson.databind.deser.DefaultDeserializationContext.readRootValue(DefaultDeserializationContext.java:342) ~[huava:2.17.2]
        at com.fasterxml.jackson.databind.ObjectMapper._readMapAndClose(ObjectMapper.java:4905) ~[huava:2.17.2]
        at com.fasterxml.jackson.databind.ObjectMapper.readValue(ObjectMapper.java:3848) ~[huava:2.17.2]
        at com.fasterxml.jackson.databind.ObjectMapper.readValue(ObjectMapper.java:3831) ~[huava:2.17.2]
        at org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService$OAuth2AuthorizationRowMapper.parseMap(JdbcOAuth2AuthorizationService.java:615) ~[huava:1.3.1]
        ... 109 common frames omitted
```

---

# Kudos to

A lot of the code in this version comes from the [quafer-engine project] (http://git.liuhung.com/gitAdmin/quafer-engine), but it's very weird that I cannot find any more information on the internet beside this link.

[StackOverflow: JwtAuthenticationToken is not in the allowlist, Jackson issue](https://stackoverflow.com/questions/70919216/jwtauthenticationtoken-is-not-in-the-allowlist-jackson-issue)

