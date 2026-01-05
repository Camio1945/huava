package cn.huava.sys.auth;

import cn.huava.sys.pojo.po.UserPo;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * used by spring
 *
 * @author Camio1945
 */
public class SysUserDetails implements UserDetails {
  private final UserPo userPo;
  private final Collection<? extends GrantedAuthority> authorities;

  public SysUserDetails(UserPo userPo, Collection<? extends GrantedAuthority> authorities) {
    this.userPo = userPo;
    this.authorities = authorities;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return userPo.getPassword();
  }

  @Override
  public String getUsername() {
    return userPo.getUsername();
  }

  @Override
  public boolean isEnabled() {
    return userPo.getIsEnabled();
  }
}
