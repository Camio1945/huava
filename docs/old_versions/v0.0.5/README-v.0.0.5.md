    
---

# v.0.0.5

Target: test Spring Security's remember-me feature; store information in MySQL.

---

# What's new?

1. In the [huava-init-v0.0.5.sql](huava-init-v0.0.5.sql) file, added a new table `persistent_logins`.

2. The SecurityConfig.java file changed:

```java

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
  private final DataSource dataSource;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
        .authorizeHttpRequests(
            registry -> {
              // registry is the type of
              // AuthorizeHttpRequestsConfigurer$AuthorizationManagerRequestMatcherRegistry
              registry.requestMatchers("/temp/test/").permitAll();
              registry.requestMatchers("/").permitAll();
              registry.anyRequest().authenticated();
            })
        .formLogin(withDefaults())
        .rememberMe(rememberMeCustomizer())
        .build();
  }

  private Customizer<RememberMeConfigurer<HttpSecurity>> rememberMeCustomizer() {
    return rememberMeConfigurer -> rememberMeConfigurer.tokenRepository(jdbcTokenRepository());
  }

  @Bean
  public JdbcTokenRepositoryImpl jdbcTokenRepository() {
    JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
    tokenRepository.setDataSource(dataSource);
    return tokenRepository;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}

```

These are new:

`private final DataSource dataSource;`

`.rememberMe(rememberMeCustomizer())`,

`public JdbcTokenRepositoryImpl jdbcTokenRepository() `


---

# Worth mentioning

---

## The steps of remember-me by DB:

1. The user signs in with a username and password and checks the remember-me checkbox.

2. The server generates a token and stores it in the `persistent_logins` table.

3. Server restart.

4. The user visits the secured URL.

5. Servers load the token from the `persistent_logins` table and store it in session.

6. From now on, the server will check the token in the session, so it doesn't need to query the database every time.

---
## Why resend /login post request by Developer tools does not work?

Short answer: The CsrfFilter is responsible.

Long answer: 

1. When you visit the http://localhost:22345/login page, the `HttpSessionCsrfTokenRepository` class will generate a CSRF token and store it in the session.

2. When the first time you send a POST request to http://localhost:22345/login, the `CsrfFilter` will check the CSRF token in the request and the session, it matches, and then the token will be removed from the session(still in the `HttpSessionCsrfTokenRepository` class).

3. When you resend the POST request to http://localhost:22345/login, the `CsrfFilter` will check the CSRF token in the request and the session, it doesn't match, a 403 status occurs, and the request will be redirected to `/error`, then the `/error` will be redirected to `/login` page. 

---

## How does Spring know the /login uri is the login api, why not /doLogin?

In the `UsernamePasswordAuthenticationFilter` class , it defines something like this:

```
new AntPathRequestMatcher("/login", "POST");
```

that how Spring knows which uri is login api and which ones are not.

