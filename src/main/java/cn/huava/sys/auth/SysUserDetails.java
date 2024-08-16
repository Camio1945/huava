package cn.huava.sys.auth;

import cn.huava.sys.pojo.po.SysUserPo;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author Camio1945
 */
public class SysUserDetails implements UserDetails {

  private final SysUserPo sysUser;

  public SysUserDetails(SysUserPo sysUser) {
    this.sysUser = sysUser;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptySet();
  }

  @Override
  public String getPassword() {
    return sysUser.getPassword();
  }

  @Override
  public String getUsername() {
    return sysUser.getLoginName();
  }

  @Override
  public boolean isAccountNonExpired() {
    // TODO
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    // TODO
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    // TODO
    return true;
  }

  @Override
  public boolean isEnabled() {
    // TODO
    return true;
  }
}
