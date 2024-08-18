package cn.huava.sys.service;

import static java.util.stream.Collectors.toSet;

import cn.huava.sys.auth.SysUserDetails;
import cn.huava.sys.mapper.*;
import cn.huava.sys.pojo.po.*;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

/**
 * load system user by username for log in (used in spring)
 *
 * @author Camio1945
 */
@Service
@RequiredArgsConstructor
public class SysUserLoginUserDetailsServiceImpl implements UserDetailsService {

  private final SysUserMapper sysUserMapper;
  private final SysRoleMapper sysRoleMapper;
  private final SysUserRoleMapper sysUserRoleMapper;

  @Override
  public UserDetails loadUserByUsername(String username) {
    Wrapper<SysUserPo> wrapper =
        new LambdaQueryWrapper<SysUserPo>().eq(SysUserPo::getUsername, username);
    SysUserPo sysUser = sysUserMapper.selectOne(wrapper);
    if (sysUser == null) {
      throw new UsernameNotFoundException("username or password error");
    }
    Set<SimpleGrantedAuthority> authorities = getAuthorities(sysUser);
    return new SysUserDetails(sysUser, authorities);
  }

  private Set<SimpleGrantedAuthority> getAuthorities(SysUserPo sysUser) {
    Wrapper<SysUserRolePo> queryWrapper =
        new LambdaQueryWrapper<SysUserRolePo>().eq(SysUserRolePo::getUserId, sysUser.getId());
    List<SysUserRolePo> userRoles = sysUserRoleMapper.selectList(queryWrapper);
    Set<SimpleGrantedAuthority> authorities = new HashSet<>();
    if (userRoles.isEmpty()) {
      return authorities;
    }
    Set<Long> roleIds = userRoles.stream().map(SysUserRolePo::getRoleId).collect(toSet());
    List<SysRolePo> roles = sysRoleMapper.selectBatchIds(roleIds);
    return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(toSet());
  }
}
