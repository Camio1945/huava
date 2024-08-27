package cn.huava.sys.service.user;

import cn.huava.common.pojo.dto.PageDto;
import cn.huava.common.pojo.qo.PageQo;
import cn.huava.common.service.BaseService;
import cn.huava.common.util.Fn;
import cn.huava.sys.mapper.UserMapper;
import cn.huava.sys.pojo.dto.UserDto;
import cn.huava.sys.pojo.dto.UserJwtDto;
import cn.huava.sys.pojo.po.*;
import cn.huava.sys.pojo.po.UserExtPo;
import cn.huava.sys.pojo.qo.LoginQo;
import cn.huava.sys.pojo.qo.UpdatePasswordQo;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AceUserService extends BaseService<UserMapper, UserExtPo> {
  private final LoginService loginService;
  private final RefreshTokenService refreshTokenService;
  private final LogoutService logoutService;
  private final UserPageService userPageService;
  private final PasswordEncoder passwordEncoder;

  public UserJwtDto login(@NonNull final HttpServletRequest req, @NonNull final LoginQo loginQo) {
    return loginService.login(req, loginQo);
  }

  public String refreshToken(@NonNull final String refreshToken) {
    return refreshTokenService.refreshToken(refreshToken);
  }

  public UserExtPo getByUserName(@NonNull final String username) {
    return getOne(
        Fn.buildUndeletedWrapper(UserExtPo::getDeleteInfo).eq(UserExtPo::getUsername, username));
  }

  public void logout(@NonNull final String refreshToken) {
    logoutService.logout(refreshToken);
  }

  public PageDto<UserDto> userPage(@NonNull PageQo<UserExtPo> pageQo, @NonNull UserExtPo params) {
    return userPageService.userPage(pageQo, params);
  }

  public boolean isUsernameExists(Long id, @NonNull String username) {
    return exists(
        Fn.buildUndeletedWrapper(UserExtPo::getDeleteInfo)
            .eq(UserExtPo::getUsername, username)
            .ne(id != null, UserExtPo::getId, id));
  }

  public void updatePassword(@NonNull UpdatePasswordQo updatePasswordQo) {
    UserPo loginUser = Fn.getLoginUser();
    String encodedNewPassword = passwordEncoder.encode(updatePasswordQo.getNewPassword());
    LambdaUpdateWrapper<UserExtPo> wrapper =
        new LambdaUpdateWrapper<UserExtPo>()
            .eq(UserExtPo::getId, loginUser.getId())
            .set(UserExtPo::getPassword, encodedNewPassword);
    update(wrapper);
  }
}
