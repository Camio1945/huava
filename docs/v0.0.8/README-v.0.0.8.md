  
---

# v.0.0.8 - Target

Implement the OAuth2 authorization server. (Note: It is just a server, not a client; it cannot be put into practice yet.)

---

# Worth mentioning

---

## 1. Where is the `Authorization` header used?

ClientSecretBasicAuthenticationConverter.convert(...):

```java
String header = request.getHeader(HttpHeaders.AUTHORIZATION);
if (header == null) {
    return null;
}
```

If `Authorization` header is present, then `OAuth2ClientAuthenticationFilter` will do the authentication.

```java
Authentication authenticationRequest = this.authenticationConverter.convert(request);
if (authenticationRequest instanceof AbstractAuthenticationToken) {
  ((AbstractAuthenticationToken) authenticationRequest)
     .setDetails(this.authenticationDetailsSource.buildDetails(request));
}
if (authenticationRequest != null) {
  validateClientIdentifier(authenticationRequest);
  Authentication authenticationResult = this.authenticationManager.authenticate(authenticationRequest);
  this.authenticationSuccessHandler.onAuthenticationSuccess(request, response, authenticationResult);
}
```

---

## 2. Why `grant_type` has to be a URL parameter and not a body parameter?

Because `OAuth2TokenEndpointFilter` has some code like this:

```java
String[] grantTypes = request.getParameterValues(OAuth2ParameterNames.GRANT_TYPE);
```

## 3. Where does Spring check for the client secret?

ClientSecretAuthenticationProvider.authenticate(...):

```java
if (!this.passwordEncoder.matches(clientSecret, registeredClient.getClientSecret())) {
   if (this.logger.isDebugEnabled()) {
      this.logger.debug(LogMessage.format(
            "Invalid request: client_secret does not match" + " for registered client '%s'",
            registeredClient.getId()));
   }
   throwInvalidClient(OAuth2ParameterNames.CLIENT_SECRET);
}
```

---

# Kudos to

[SpringBootOAuth2 Project](https://github.com/wdkeyser02/SpringBootOAuth2)
