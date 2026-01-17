package cn.huava.sys.service.user;

import cn.huava.common.service.BaseService;
import cn.huava.sys.mapper.UserMapper;
import cn.huava.sys.pojo.po.RefreshTokenPo;
import cn.huava.sys.pojo.po.UserExtPo;
import cn.huava.sys.service.refreshtoken.AceRefreshTokenService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;

/**
 * @author Camio1945
 */
@Slf4j
@Service
@NullMarked
@RequiredArgsConstructor
class LogoutService extends BaseService<UserMapper, UserExtPo> {

  private final AceRefreshTokenService aceRefreshTokenService;

  protected void logout(final String refreshToken) {
    RefreshTokenPo refreshTokenPo = aceRefreshTokenService.getByRefreshToken(refreshToken);
    if (refreshTokenPo != null) {
      aceRefreshTokenService.softDelete(refreshTokenPo.getId());
    }
  }
}
