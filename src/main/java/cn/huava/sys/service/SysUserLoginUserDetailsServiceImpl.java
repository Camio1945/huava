package cn.huava.sys.service;

import cn.huava.sys.auth.SysUserDetails;
import cn.huava.sys.mapper.*;
import cn.huava.sys.pojo.po.*;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

/**
 * load system user by username for login
 *
 * @author Camio1945
 */
@Service
@AllArgsConstructor
public class SysUserLoginUserDetailsServiceImpl implements UserDetailsService {

  private final SysUserMapper sysUserMapper;
  private final SysRoleMapper sysRoleMapper;

  @Override
  public UserDetails loadUserByUsername(String username) {
    Wrapper<SysUserPo> wrapper =
        new LambdaQueryWrapper<SysUserPo>().eq(SysUserPo::getLoginName, username);
    SysUserPo sysUser = sysUserMapper.selectOne(wrapper);
    if (sysUser == null) {
      throw new UsernameNotFoundException("username or password error");
    }
    return new SysUserDetails(sysUser);
  }

}
