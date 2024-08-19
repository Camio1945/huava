package cn.huava.sys.service.sysuser;

import cn.huava.common.service.BaseService;
import cn.huava.sys.mapper.SysUserMapper;
import cn.huava.sys.pojo.dto.SysUserJwtDto;
import cn.huava.sys.pojo.po.SysUserPo;
import cn.huava.sys.pojo.qo.LoginQo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AceSysUserService extends BaseService<SysUserMapper, SysUserPo> {
  private final LoginService loginService;
  private final RefreshTokenService refreshTokenService;

  public SysUserJwtDto login(HttpServletRequest req, LoginQo loginQo) {
    return loginService.login(req, loginQo);
  }

  public String refreshToken(String refreshToken) {
    return refreshTokenService.refreshToken(refreshToken);
  }
}
