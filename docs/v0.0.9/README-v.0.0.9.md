  
---

# v.0.0.9 - Target

When users send a log-in request, they don't need the `Authorization` header or the `grant_type` parameter; they just need the username and password, and since the `Authorization` contains a secret, it shouldn't be sent by the browser. Instead, the server will build a new request for the oauth2 server.

So, for the user, it's one request. But for the server, it's two requests.

I know it's unnecessary and inefficient to use OAuth 2.0 in a monolithic application, but I want to learn how to use it, so it's a selfish choice.

---

# Worth mentioning

1. The `SysUserLoginServic` class is responsible for handling the log-in request from the user and then building a new request to send to the oauth2 server itself.


