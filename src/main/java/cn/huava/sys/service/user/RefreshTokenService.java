package cn.huava.sys.service.user;

import cn.huava.common.constant.CommonConstant;
import cn.huava.common.service.BaseService;
import cn.huava.sys.mapper.UserMapper;
import cn.huava.sys.pojo.dto.UserJwtDto;
import cn.huava.sys.pojo.po.RefreshTokenPo;
import cn.huava.sys.pojo.po.UserExtPo;
import cn.huava.sys.service.jwt.AceJwtService;
import cn.huava.sys.service.refreshtoken.AceRefreshTokenService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.v7.json.jwt.*;
import org.springframework.stereotype.Service;

/**
 * 退出登录
 *
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
class RefreshTokenService extends BaseService<UserMapper, UserExtPo> {

  private final AceRefreshTokenService aceRefreshTokenService;
  private final AceJwtService aceJwtService;

  protected String refreshToken(@NonNull String refreshToken) {
    RefreshTokenPo po = aceRefreshTokenService.getByRefreshToken(refreshToken);
    if (po == null || po.getDeleteInfo() > 0) {
      throw new IllegalArgumentException("Refresh token invalid");
    }
    JWT jwt = JWTUtil.parseToken(refreshToken);
    Long exp = jwt.getPayload("exp", Long.class);
    if (exp == null || exp * CommonConstant.MILLIS_PER_SECOND < System.currentTimeMillis()) {
      throw new IllegalArgumentException("Refresh token expired");
    }
    UserJwtDto res = aceJwtService.createToken(po.getSysUserId());
    return res.getAccessToken();
  }
}
