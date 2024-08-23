package cn.huava.sys.service.user;

import cn.huava.common.service.BaseService;
import cn.huava.common.util.Fn;
import cn.huava.sys.mapper.UserMapper;
import cn.huava.sys.pojo.dto.UserJwtDto;
import cn.huava.sys.pojo.po.UserPo;
import cn.huava.sys.pojo.qo.LoginQo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AceUserService extends BaseService<UserMapper, UserPo> {
  private final LoginService loginService;
  private final RefreshTokenService refreshTokenService;
  private final LogoutService logoutService;

  public UserJwtDto login(@NonNull final HttpServletRequest req, @NonNull final LoginQo loginQo) {
    return loginService.login(req, loginQo);
  }

  public String refreshToken(@NonNull final String refreshToken) {
    return refreshTokenService.refreshToken(refreshToken);
  }

  public UserPo getByUserName(@NonNull final String username) {
    return getOne(
        Fn.buildUndeletedWrapper(UserPo::getDeleteInfo).eq(UserPo::getUsername, username));
  }

  public void logout(@NonNull final String refreshToken) {
    logoutService.logout(refreshToken);
  }
}
