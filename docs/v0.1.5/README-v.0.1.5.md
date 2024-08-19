  
---

# v.0.1.5 - Target

## 1. Change the captcha logic:

1. If it's not GraalVM native image mode, use local dynamic captcha (use java.awt to generate every time).

2. If it is GraalVM native image mode, try to use the online captcha API first.

3. If step 2 fails, use the local static captcha (the static images that have already been generated).

Reason: The local captcha relies on the `java.awt` package, which is not supported by the GraalVM native image mode.

---
## 2. Edit login api, add captcha verification

---

# Worth mentioning

## Http requests got messy code

**Error Info:**

1. Normal http requests work fine in both JVM mode and native image mode

2. Http requests with the Content-Type `gzip` work fine in JVM mode, but get messy code in native image mode

**Solution:** 

add `GZIPInputStream.class` in RuntimeHintsRegistrarConfig.java


