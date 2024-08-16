package cn.huava.common.filter;

import cn.huava.sys.auth.SysUserUserDetails;
import cn.huava.sys.pojo.po.SysUserPo;
import cn.huava.sys.service.jwt.JwtAceService;
import cn.huava.sys.service.sysuser.SysUserAceService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author Camio1945
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtAceService jwtAceService;

  private final SysUserAceService sysUserAceService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    String token = getTokenFromRequest(request);
    if (StringUtils.hasText(token) && jwtAceService.verifyToken(token)) {
      Long userId = jwtAceService.getUserIdFromAccessToken(token);
      String username = sysUserAceService.getById(userId).getLoginName();
      UserDetails userDetails = buildUserDetails(username);
      UsernamePasswordAuthenticationToken authenticationToken =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
    filterChain.doFilter(request, response);
  }

  private String getTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(BEARER_PREFIX.length());
    }
    return null;
  }

  private UserDetails buildUserDetails(String username) {
    SysUserPo sysUser = new SysUserPo();
    sysUser.setLoginName(username);
    return new SysUserUserDetails(sysUser, new HashSet<>());
  }
}
