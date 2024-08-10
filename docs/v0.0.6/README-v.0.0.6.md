    
---

# v.0.0.6 - Target

Login with JWT.

---

# What's new?

1. CSRF is disabled.
2. Added `@EnableMethodSecurity` on the `SecurityConfig` class.
3. Added `JwtAuthenticationFilter`, `JwtTokenProvider`, `SysUserJwtAuthController`, and some other classes.
4. Added 2 new tables: `sys_role`, `sys_user_role`, and their corresponding classes.

---

# How to use?

1. Download the code for tag v.0.0.6.

2. Use MySQL 8's root account to execute the SQL statements in the file huava-init-v0.0.6.sql

3. Run HuavaApplication.java

4. Use [Bruno](https://www.usebruno.com/) open [bruno_scripts_v0.0.6](bruno_scripts_v0.0.6) folder as a collection.

5. Run the `1_sysUserLoginWithJwt` api with Bruno. This api will log in with username john and password john, and return a JWT access token, copy the token (needed by next step).

6. Open the `2_securedAdmin` api with Bruno, go to `Auth` tab, choose `Bearer Token`, paste the token in the `Token` field, and run the api. If every thing goes well, you will get a `"Hello, Secured Admin!"`  response, because john has role `ROLE_ADMIN`. 

7. Open the `3_securedUser` api with Bruno, go to `Auth` tab, choose `Bearer Token`, paste the token in the `Token` field, and run the api. If every thing goes well, you will get a `403 Forbidden` response, because john has no role `ROLE_USER`.

---

# Worth mentioning

---

## 1. csrf is disabled for these reasons

1. Stateless Authentication: JWT is typically used in stateless authentication scenarios where the server does not maintain any session state between requests. CSRF protection is primarily designed to protect stateful applications where a session is maintained, making it less relevant for stateless authentication with JWT.

2. Token-based Authentication: With JWT, the authentication mechanism relies on tokens that are included in HTTP headers (e.g., Authorization header) rather than cookies. CSRF attacks target cookies, so token-based systems are less vulnerable to these attacks.

3. Security Model: In a stateless JWT-based authentication model, disabling CSRF is often a trade-off to simplify the security model and avoid potential issues with session management.

---

## 2. How to generate JWT Secret Key?

Pure java code:

```java
// length means (32 bytes are required for 256-bit key)
int length = 32;
// Create a byte array to hold the random bytes
byte[] keyBytes = new byte[length];
// Generate the random bytes
new java.security.SecureRandom().nextBytes(keyBytes);
// Encode the key in Base64 format for easier storage and usage
String secretKey = Base64.getEncoder().encodeToString(keyBytes);
```

---

## 3. Should we store the roles info into the JWT token?

Short answer: Yes, the advantages outweigh disadvantages.

Long answer: Go ask ChatGPT.

---

# Kudos to

LakshithaFdo's [blog](https://medium.com/@Lakshitha_Fernando/jwt-spring-security-6-and-spring-boot-3-with-simple-project-819d84e09af2) and [GitHub repo](https://github.com/LakshithaFdo/Spring-Security).





