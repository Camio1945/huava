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
public class UserLoginUserDetailsServiceImpl implements UserDetailsService {

  private final UserMapper userMapper;
  private final RoleMapper roleMapper;
  private final UserRoleMapper userRoleMapper;

  @Override
  public UserDetails loadUserByUsername(String username) {
    Wrapper<UserPo> wrapper = new LambdaQueryWrapper<UserPo>().eq(UserPo::getUsername, username);
    UserPo userPo = userMapper.selectOne(wrapper);
    if (userPo == null) {
      throw new UsernameNotFoundException("username or password error");
    }
    Set<SimpleGrantedAuthority> authorities = getAuthorities(userPo);
    return new SysUserDetails(userPo, authorities);
  }

  private Set<SimpleGrantedAuthority> getAuthorities(UserPo userPo) {
    Wrapper<UserRolePo> queryWrapper =
        new LambdaQueryWrapper<UserRolePo>().eq(UserRolePo::getUserId, userPo.getId());
    List<UserRolePo> userRoles = userRoleMapper.selectList(queryWrapper);
    Set<SimpleGrantedAuthority> authorities = new HashSet<>();
    if (userRoles.isEmpty()) {
      return authorities;
    }
    Set<Long> roleIds = userRoles.stream().map(UserRolePo::getRoleId).collect(toSet());
    List<RolePo> roles = roleMapper.selectBatchIds(roleIds);
    return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(toSet());
  }
}
