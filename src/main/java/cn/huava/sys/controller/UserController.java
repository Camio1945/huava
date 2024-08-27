package cn.huava.sys.controller;

import cn.huava.common.controller.BaseController;
import cn.huava.common.pojo.dto.PageDto;
import cn.huava.common.pojo.qo.PageQo;
import cn.huava.common.util.Fn;
import cn.huava.sys.mapper.UserMapper;
import cn.huava.sys.pojo.dto.*;
import cn.huava.sys.pojo.po.*;
import cn.huava.sys.pojo.po.UserExtPo;
import cn.huava.sys.pojo.qo.LoginQo;
import cn.huava.sys.pojo.qo.UpdatePasswordQo;
import cn.huava.sys.service.role.AceRoleService;
import cn.huava.sys.service.user.AceUserService;
import cn.huava.sys.service.userrole.AceUserRoleService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Camio1945
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sys/user")
public class UserController extends BaseController<AceUserService, UserMapper, UserExtPo> {
  private final AceRoleService roleService;
  private final AceUserRoleService userRoleService;

  @GetMapping("/page")
  public ResponseEntity<PageDto<UserDto>> page(
      @NonNull final PageQo<UserExtPo> pageQo, @NonNull final UserExtPo params) {
    PageDto<UserDto> pageDto = service.userPage(pageQo, params);
    return ResponseEntity.ok(pageDto);
  }

  @GetMapping("/code")
  public ResponseEntity<String> code(@NonNull final HttpServletRequest req) {
    String url =
        Fn.format("{}://{}:{}/captcha", req.getScheme(), req.getServerName(), req.getServerPort());
    return ResponseEntity.ok(url);
  }

  @PostMapping("/login")
  public ResponseEntity<UserJwtDto> login(
      @NonNull final HttpServletRequest req, @RequestBody @NonNull final LoginQo loginQo) {
    return ResponseEntity.ok(service.login(req, loginQo));
  }

  @GetMapping("/info")
  public ResponseEntity<UserInfoDto> info() {
    UserPo loginUser = Fn.getLoginUser();
    List<String> roleNames = roleService.getRoleNamesByUserId(loginUser.getId());
    UserInfoDto userInfoDto = new UserInfoDto(loginUser.getUsername(), roleNames);
    return ResponseEntity.ok(userInfoDto);
  }

  @PostMapping("/refreshToken")
  public ResponseEntity<String> refreshToken(@RequestBody @NonNull String refreshToken) {
    String accessToken = service.refreshToken(refreshToken);
    return ResponseEntity.ok(accessToken);
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@RequestBody @NonNull String refreshToken) {
    service.logout(refreshToken);
    return ResponseEntity.ok(null);
  }

  @GetMapping("/isUsernameExists")
  public ResponseEntity<Boolean> isUsernameExists(final Long id, @NonNull final String username) {
    return ResponseEntity.ok(service.isUsernameExists(id, username));
  }

  @Override
  protected void afterGetById(UserExtPo entity) {
    entity.setPassword(null);
    entity.setRoleIds(userRoleService.getRoleIdsByUserId(entity.getId()));
  }

  @Override
  protected void beforeSave(UserExtPo entity) {
    entity.setPassword(Fn.encryptPassword(entity.getPassword()));
  }

  @Override
  protected void afterSave(UserExtPo entity) {
    userRoleService.saveUserRole(entity.getId(), entity.getRoleIds());
  }

  /**
   * If password is empty, then use the password stored in the database.<br>
   * If password is not empty, then encrypt it.<br>
   *
   * @param entity The entity to be updated.
   */
  @Override
  protected void beforeUpdate(UserExtPo entity) {
    String password = entity.getPassword();
    if (Fn.isBlank(password)) {
      String dbPassword = service.getById(entity.getId()).getPassword();
      entity.setPassword(dbPassword);
    } else {
      entity.setPassword(Fn.encryptPassword(password));
    }
  }

  @Override
  protected void afterUpdate(UserExtPo entity) {
    userRoleService.saveUserRole(entity.getId(), entity.getRoleIds());
  }

  @PatchMapping("/updatePassword")
  public ResponseEntity<Void> updatePassword(
      @RequestBody @NonNull @Validated UpdatePasswordQo updatePasswordQo) {
    service.updatePassword(updatePasswordQo);
    return ResponseEntity.ok(null);
  }
}
