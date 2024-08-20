package cn.huava.sys.service.sysuser;

import cn.huava.common.constant.CommonConstant;
import cn.huava.common.service.BaseService;
import cn.huava.sys.mapper.SysUserMapper;
import cn.huava.sys.pojo.dto.SysUserJwtDto;
import cn.huava.sys.pojo.po.SysRefreshTokenPo;
import cn.huava.sys.pojo.po.SysUserPo;
import cn.huava.sys.service.jwt.AceJwtService;
import cn.huava.sys.service.sysrefreshtoken.AceSysRefreshTokenService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.json.jwt.*;
import org.springframework.stereotype.Service;

/**
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
class RefreshTokenService extends BaseService<SysUserMapper, SysUserPo> {

  private final AceSysRefreshTokenService sysRefreshTokenAceService;
  private final AceJwtService jwtAceService;

  protected String refreshToken(@NonNull String refreshToken) {
    SysRefreshTokenPo po = sysRefreshTokenAceService.getByRefreshToken(refreshToken);
    if (po == null || po.getDeleteInfo() > 0) {
      throw new IllegalArgumentException("refresh token invalid");
    }
    JWT jwt = JWTUtil.parseToken(refreshToken);
    Long exp = jwt.getPayload("exp", Long.class);
    if (exp == null || exp * CommonConstant.MILLIS_PER_SECOND < System.currentTimeMillis()) {
      throw new IllegalArgumentException("refresh token expired");
    }
    SysUserJwtDto res = jwtAceService.createToken(po.getSysUserId());
    return res.getAccessToken();
  }
}