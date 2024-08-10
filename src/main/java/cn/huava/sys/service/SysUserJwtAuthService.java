package cn.huava.sys.service;

import cn.huava.common.provider.JwtTokenProvider;
import cn.huava.sys.pojo.query.LoginQuery;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * jwt auth for system user
 *
 * @author Camio1945
 */
@Service
@AllArgsConstructor
public class SysUserJwtAuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;

  public String login(LoginQuery loginQuery) {
    String username = loginQuery.getUsername();
    String password = loginQuery.getPassword();
    Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
    authentication = authenticationManager.authenticate(authentication);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    return jwtTokenProvider.generateToken(authentication);
  }
}
