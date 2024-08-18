package cn.huava.sys.auth;

import cn.huava.sys.pojo.po.SysUserPo;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * used by spring
 * @author Camio1945
 */
public class SysUserDetails implements UserDetails {
  private final SysUserPo sysUser;
  private final Collection<? extends GrantedAuthority> authorities;

  public SysUserDetails(SysUserPo sysUser, Collection<? extends GrantedAuthority> authorities) {
    this.sysUser = sysUser;
    this.authorities = authorities;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return sysUser.getPassword();
  }

  @Override
  public String getUsername() {
    return sysUser.getUsername();
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
