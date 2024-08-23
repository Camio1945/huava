package cn.huava.sys.service.user;

import cn.huava.common.service.BaseService;
import cn.huava.sys.mapper.UserMapper;
import cn.huava.sys.pojo.po.RefreshTokenPo;
import cn.huava.sys.pojo.po.UserPo;
import cn.huava.sys.service.refreshtoken.AceRefreshTokenService;
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
class LogoutService extends BaseService<UserMapper, UserPo> {

  private final AceRefreshTokenService aceRefreshTokenService;

  protected void logout(@NonNull final String refreshToken) {
    RefreshTokenPo refreshTokenPo = aceRefreshTokenService.getByRefreshToken(refreshToken);
    if (refreshTokenPo != null) {
      aceRefreshTokenService.softDelete(refreshTokenPo.getId());
    }
  }
}
