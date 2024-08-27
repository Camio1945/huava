    
---

# v.0.0.4

Target: test Spring Security's default remember-me feature.

---

# Spring Security Remember-Me

In the SecurityConfig.securityFilterChain(...) method, should plus:

```
.rememberMe(withDefaults())
```

Key class and method:

```
AbstractRememberMeServices.setCookie(...)
```

The username, password, expire time will be stored in the cookie in web browser's cookie.


Note: after [02.visit_secured_url_1st_time_with_remember_me_cookie_after_restarting_browser.puml](02.visit_secured_url_1st_time_with_remember_me_cookie_after_restarting_browser.puml), when visiting any secured URL, the `rememberMeAuth` can be found in the session, so there will be no need to load the user from the database again.
