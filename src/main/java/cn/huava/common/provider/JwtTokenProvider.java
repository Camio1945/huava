package cn.huava.common.provider;

import static java.util.stream.Collectors.joining;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * @author Camio1945
 */
@Component
public class JwtTokenProvider {

  /** Note: This secret key should be different for every project */
  private static final String JWT_SECRET = "XxnrSwmTsVied9ZbBx4Yw+99qrISweNHHbni6eMyhAw=";

  private static final long JWT_EXPIRY_TIME_MS = Duration.ofHours(1).getSeconds() * 1000;
  private static final String ROLES = "roles";

  public String generateToken(Authentication authentication) {
    String username = authentication.getName();
    Date currentDate = new Date();
    Date expireDate = new Date(currentDate.getTime() + JWT_EXPIRY_TIME_MS);
    String roles = getRoles(authentication);
    return Jwts.builder()
        .subject(username)
        .claim(ROLES, roles)
        .issuedAt(new Date())
        .expiration(expireDate)
        .signWith(key(), Jwts.SIG.HS256)
        .compact();
  }

  private static String getRoles(Authentication authentication) {
    return authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(joining(","));
  }

  private SecretKey key() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET));
  }

  public String getUsername(String token) {
    return Jwts.parser()
        .verifyWith(key())
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
  }

  public String getRoles(String token) {
    return Jwts.parser()
        .verifyWith(key())
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .get(ROLES)
        .toString();
  }

  public boolean validateToken(String token) {
    Jwts.parser().verifyWith(key()).build().parse(token);
    return true;
  }
}
