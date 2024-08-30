package cn.huava.common.filter;

import cn.huava.common.config.SecurityConfig;
import cn.huava.common.constant.CommonConstant;
import cn.huava.common.util.Fn;
import cn.huava.sys.cache.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Check if the user has permission to access the uri. <br>
 *
 * <pre>
 * 1. If user is not login, then allow access.
 *    If the uri is allowed in {@link SecurityConfig#securityFilterChain}, then allow access.
 *    If the uri is not allowed in {@link SecurityConfig#securityFilterChain}, then .
 * </pre>
 *
 * @author Camio1945
 */
@Component
@RequiredArgsConstructor
public class UriAuthFilter extends OncePerRequestFilter {
  private static final String URI_AUTH_RANGE_MAIN = "main";

  private static final String[] MAIN_URI_SUFFIX = {"/create", "/delete", "/update", "/page"};

  private final UserRoleCache userRoleCache;

  private final RoleCache roleCache;

  @Value("${project.api_auth_range}")
  private String uriAuthRange;

  @Override
  protected void doFilterInternal(
      @NonNull final HttpServletRequest request,
      @NonNull final HttpServletResponse response,
      @NonNull final FilterChain filterChain)
      throws ServletException, IOException {
    if (!hasPerm(request)) {
      writeResponse(response);
      return;
    }
    filterChain.doFilter(request, response);
  }

  private boolean hasPerm(HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    // not login
    if (authentication == null) {
      return true;
    }
    String uri = getUri(request);
    if (!shouldCheckPermission(uri)) {
      return true;
    }
    List<Long> roleIds = getRoleIds();
    boolean hasPerm = false;
    for (Long roleId : roleIds) {
      if (roleId == CommonConstant.ADMIN_ROLE_ID
          || roleCache.getPermUrisByRoleId(roleId).contains(uri)) {
        hasPerm = true;
        break;
      }
    }
    return hasPerm;
  }

  private static void writeResponse(HttpServletResponse response) throws IOException {
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType("text/plain; charset=UTF-8");
    PrintWriter writer = response.getWriter();
    writer.write("无权访问");
    writer.flush();
  }

  private static String getUri(HttpServletRequest request) {
    String uri = request.getRequestURI();
    uri = uri.replaceAll("\\d+$", "");
    return uri;
  }

  /**
   * If project.api_auth_range == main, then only apis end with {@link #MAIN_URI_SUFFIX} will be
   * checked, otherwise, all apis will be checked.
   */
  private boolean shouldCheckPermission(String uri) {
    if (URI_AUTH_RANGE_MAIN.equals(uriAuthRange)) {
      for (String mainUriSuffix : MAIN_URI_SUFFIX) {
        if (uri.endsWith(mainUriSuffix)) {
          return true;
        }
      }
    }
    return false;
  }

  private List<Long> getRoleIds() {
    Long userId = Fn.getLoginUser().getId();
    return userRoleCache.getRoleIdsByUserId(userId);
  }
}
