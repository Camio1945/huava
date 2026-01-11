package cn.huava.common.util;

import cn.huava.sys.cache.UserCache;
import cn.huava.sys.pojo.po.UserExtPo;
import cn.huava.sys.pojo.po.UserPo;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author Camio1945
 */
@NullMarked
class LoginUtil {
  private LoginUtil() {}

  /** This method is intentionally protected, please use Fn.getLoginUser() as the only entry. */
  protected static UserPo getLoginUser() {
    UsernamePasswordAuthenticationToken authentication =
        (UsernamePasswordAuthenticationToken)
            SecurityContextHolder.getContext().getAuthentication();
    assert authentication != null;
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    assert userDetails != null;
    String username = userDetails.getUsername();
    UserCache userCache = Fn.getBean(UserCache.class);
    Long id = userCache.getIdByUsername(username);
    return userCache.getById(id);
  }
}
