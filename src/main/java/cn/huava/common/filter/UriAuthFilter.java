package cn.huava.common.filter;

import cn.huava.common.config.SecurityConfig;
import cn.huava.common.constant.CommonConstant;
import cn.huava.common.util.Fn;
import cn.huava.sys.cache.*;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import lombok.*;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 检查用户是否有权限访问 uri 路径<br>
 *
 * <pre>
 * 1. 如果用户已经登录了，则检查用户是否有权限访问 uri 路径。
 * 2. 如果用户没有登录，则本过滤器不做任何拦截，此时可以细分成两种情况：
 *    2.1. uri 路径在 {@link SecurityConfig#registerFreeUris} 中，说明不需要鉴权就可以访问，那就直接访问。
 *    2.2. uri 路径不在 {@link SecurityConfig#registerFreeUris} 中，说明需要鉴权才能访问，那么由于用户没有登录，
 *         在后续的 {@link AuthorizationFilter#doFilter} 方法的的这一行代码就会发现没有权限：
 *           AuthorizationDecision decision = this.authorizationManager.check(this::getAuthentication, request);
 *         于是就会拒绝访问。
 * </pre>
 *
 * @author Camio1945
 */
@NullMarked
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
      final HttpServletRequest request,
      final HttpServletResponse response,
      final FilterChain filterChain)
      throws ServletException, IOException {
    if (!hasPerm(request)) {
      writeResponse(response);
      return;
    }
    filterChain.doFilter(request, response);
  }

  private boolean hasPerm(HttpServletRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    // 未登录
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

  /** 见 application.yml 文件中关于 api_auth_range 的注释 */
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
