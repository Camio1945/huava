    
---

# v.0.0.5

Target: test Spring Security's remember-me feature; store information in MySQL.

---

# What new?

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

The steps of remember-me by DB:

1. The user signs in with a username and password and checks the remember-me checkbox.

2. The server generates a token and stores it in the `persistent_logins` table.

3. Server restart.

4. The user visits the secured URL.

5. Servers load the token from the `persistent_logins` table and store it in session.

6. From now on, the server will check the token in the session, so it doesn't need to query the database every time.

