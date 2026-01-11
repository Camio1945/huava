package cn.huava.sys.service.user;

import cn.huava.common.pojo.dto.PageDto;
import cn.huava.common.pojo.qo.PageQo;
import cn.huava.common.service.BaseService;
import cn.huava.common.util.Fn;
import cn.huava.sys.cache.UserCache;
import cn.huava.sys.mapper.UserMapper;
import cn.huava.sys.pojo.dto.*;
import cn.huava.sys.pojo.po.*;
import cn.huava.sys.pojo.po.UserExtPo;
import cn.huava.sys.pojo.qo.LoginQo;
import cn.huava.sys.pojo.qo.UpdatePasswordQo;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务主入口类<br>
 *
 * @author Camio1945
 */
@Slf4j
@Service
@NullMarked
@RequiredArgsConstructor
public class AceUserService extends BaseService<UserMapper, UserExtPo> {
  private final LoginService loginService;
  private final RefreshTokenService refreshTokenService;
  private final GetUserInfoService getUserInfoService;
  private final LogoutService logoutService;
  private final UserPageService userPageService;
  private final PasswordEncoder passwordEncoder;
  private final UserCache userCache;

  public UserJwtDto login(final HttpServletRequest req, final LoginQo loginQo) {
    return loginService.login(req, loginQo);
  }

  public String refreshToken(final String refreshToken) {
    return refreshTokenService.refreshToken(refreshToken);
  }

  public void logout(final String refreshToken) {
    logoutService.logout(refreshToken);
  }

  public PageDto<UserDto> userPage(PageQo<UserExtPo> pageQo, UserExtPo params) {
    return userPageService.userPage(pageQo, params);
  }

  public boolean isUsernameExists(@Nullable Long neId, String username) {
    return exists(
        Fn.undeletedWrapper(UserExtPo::getDeleteInfo)
            .eq(UserExtPo::getUsername, username)
            .ne(neId != null, UserExtPo::getId, neId));
  }

  public void updatePassword(UpdatePasswordQo updatePasswordQo) {
    UserPo loginUser = Fn.getLoginUser();
    String encodedNewPassword = passwordEncoder.encode(updatePasswordQo.getNewPassword());
    LambdaUpdateWrapper<UserExtPo> wrapper =
        new LambdaUpdateWrapper<UserExtPo>()
            .eq(UserExtPo::getId, loginUser.getId())
            .set(UserExtPo::getPassword, encodedNewPassword);
    update(wrapper);
    userCache.afterSaveOrUpdate((UserExtPo) loginUser);
  }

  public UserInfoDto getUserInfoDto() {
    return getUserInfoService.getUserInfoDto();
  }
}
