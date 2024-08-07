package cn.huava.sys.service;

import cn.huava.sys.auth.SysUserUserDetails;
import cn.huava.sys.mapper.SysUserMapper;
import cn.huava.sys.pojo.po.SysUser;
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

  @Override
  public UserDetails loadUserByUsername(String username) {
    Wrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>().eq(SysUser::getLoginName, username);
    SysUser sysUser = sysUserMapper.selectOne(wrapper);
    if (sysUser == null) {
      throw new UsernameNotFoundException("username or password error");
    }
    return new SysUserUserDetails(sysUser);
  }
}
