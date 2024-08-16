package cn.huava.sys.service.sysuser;

import cn.huava.sys.mapper.SysUserMapper;
import cn.huava.sys.pojo.dto.SysUserJwtDto;
import cn.huava.sys.pojo.po.SysUserPo;
import cn.huava.sys.pojo.qo.LoginQo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserAceService extends ServiceImpl<SysUserMapper, SysUserPo> {
  private final SysUserLoginService loginService;
  private final SysUserRefreshTokenService refreshTokenService;

  public SysUserJwtDto login(LoginQo loginQo) {
    return loginService.login(loginQo);
  }

  public String refreshToken(String refreshToken) {
    return refreshTokenService.refreshToken(refreshToken);
  }
}
