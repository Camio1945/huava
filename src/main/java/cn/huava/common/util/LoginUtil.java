package cn.huava.common.util;

import cn.huava.sys.pojo.po.UserPo;
import cn.huava.sys.service.user.AceUserService;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author Camio1945
 */
public class LoginUtil {
  private LoginUtil() {}

  /** This method is intentionally protected, please use Fn.getLoginUser() as the only entry. */
  protected static @NonNull UserPo getLoginUser() {
    UsernamePasswordAuthenticationToken authentication =
        (UsernamePasswordAuthenticationToken)
            SecurityContextHolder.getContext().getAuthentication();
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    String username = userDetails.getUsername();
    return Fn.getBean(AceUserService.class).getByUserName(username);
  }
}
