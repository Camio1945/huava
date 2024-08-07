package cn.huava.sys.auth;

import cn.huava.sys.pojo.po.SysUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class SysUserUserDetails implements UserDetails {
    private final SysUser sysUser;

    public SysUserUserDetails(SysUser sysUser) {
      this.sysUser = sysUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      return List.of();
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
