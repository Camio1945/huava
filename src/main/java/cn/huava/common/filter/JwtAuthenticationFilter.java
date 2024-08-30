package cn.huava.common.filter;

import static cn.huava.common.constant.CommonConstant.AUTHORIZATION_HEADER;
import static cn.huava.common.constant.CommonConstant.REFRESH_TOKEN_URI;

import cn.huava.sys.auth.SysUserDetails;
import cn.huava.sys.cache.UserCache;
import cn.huava.sys.pojo.po.UserPo;
import cn.huava.sys.service.jwt.AceJwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
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

  private final AceJwtService jwtAceService;

  private final UserCache userCache;

  @Override
  protected void doFilterInternal(
      @NonNull final HttpServletRequest request,
      @NonNull final HttpServletResponse response,
      @NonNull final FilterChain filterChain)
      throws ServletException, IOException {
    if (!request.getRequestURI().equals(REFRESH_TOKEN_URI)) {
      String token = getTokenFromRequest(request);
      if (StringUtils.hasText(token)) {
        if (jwtAceService.isTokenExpired(token)) {
          writeResponse(response);
          return;
        }
        setAuthentication(request, token);
      }
    }
    filterChain.doFilter(request, response);
  }

  private String getTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
      return bearerToken.substring(BEARER_PREFIX.length());
    }
    return null;
  }

  private static void writeResponse(HttpServletResponse response) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    PrintWriter writer = response.getWriter();
    writer.write("Access token expired");
    writer.flush();
  }

  private void setAuthentication(HttpServletRequest request, String token) {
    Long userId = jwtAceService.getUserIdFromAccessToken(token);
    String username = userCache.getById(userId).getUsername();
    UserDetails userDetails = buildUserDetails(username);
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
  }

  private UserDetails buildUserDetails(String username) {
    UserPo userPo = new UserPo();
    userPo.setUsername(username);
    return new SysUserDetails(userPo, new HashSet<>());
  }
}
