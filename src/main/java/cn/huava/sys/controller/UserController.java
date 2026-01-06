package cn.huava.sys.controller;

import cn.huava.common.controller.BaseController;
import cn.huava.common.pojo.dto.PageDto;
import cn.huava.common.pojo.qo.PageQo;
import cn.huava.common.util.Fn;
import cn.huava.sys.cache.UserCache;
import cn.huava.sys.cache.UserRoleCache;
import cn.huava.sys.mapper.UserMapper;
import cn.huava.sys.pojo.dto.*;
import cn.huava.sys.pojo.po.UserExtPo;
import cn.huava.sys.pojo.qo.LoginQo;
import cn.huava.sys.pojo.qo.UpdatePasswordQo;
import cn.huava.sys.service.user.AceUserService;
import cn.huava.sys.service.userrole.AceUserRoleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 *
 * @author Camio1945
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sys/user")
public class UserController extends BaseController<AceUserService, UserMapper, UserExtPo> {
  private final AceUserRoleService userRoleService;
  private final UserCache userCache;
  private final UserRoleCache userRoleCache;

  @GetMapping("/page")
  public ResponseEntity<PageDto<UserDto>> page(
      @NonNull final PageQo<UserExtPo> pageQo, @NonNull final UserExtPo params) {
    PageDto<UserDto> pageDto = service.userPage(pageQo, params);
    return ResponseEntity.ok(pageDto);
  }

  @PostMapping("/login")
  public ResponseEntity<UserJwtDto> login(
      @NonNull final HttpServletRequest req, @RequestBody @NonNull final LoginQo loginQo) {
    return ResponseEntity.ok(service.login(req, loginQo));
  }

  @GetMapping("/myself")
  public ResponseEntity<UserInfoDto> myself() {
    UserInfoDto userInfoDto = service.getUserInfoDto();
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
  public ResponseEntity<Boolean> isUsernameExists(final Long neId, @NonNull final String username) {
    return ResponseEntity.ok(service.isUsernameExists(neId, username));
  }

  @PatchMapping("/updatePassword")
  public ResponseEntity<Void> updatePassword(
      @RequestBody @NonNull @Validated UpdatePasswordQo updatePasswordQo) {
    service.updatePassword(updatePasswordQo);
    return ResponseEntity.ok(null);
  }

  @Override
  protected void afterGetById(@NonNull UserExtPo entity) {
    entity.setPassword(null);
    entity.setRoleIds(userRoleService.getRoleIdsByUserId(entity.getId()));
  }

  @Override
  protected void beforeSave(@NonNull UserExtPo entity) {
    entity.setPassword(Fn.encryptPassword(entity.getPassword()));
  }

  @Override
  protected void afterSave(@NonNull UserExtPo entity) {
    afterSaveOrUpdate(entity);
  }

  /**
   * If password is empty, then use the password stored in the database.<br>
   * If password is not empty, then encrypt it.<br>
   *
   * @param entity The entity to be updated.
   */
  @Override
  protected void beforeUpdate(@NonNull UserExtPo entity) {
    String password = entity.getPassword();
    UserExtPo before = userCache.getById(entity.getId());
    if (Fn.isBlank(password)) {
      String dbPassword = before.getPassword();
      entity.setPassword(dbPassword);
    } else {
      entity.setPassword(Fn.encryptPassword(password));
    }
    userCache.beforeUpdate(before);
  }

  @Override
  protected void afterUpdate(@NonNull UserExtPo entity) {
    afterSaveOrUpdate(entity);
  }

  @Override
  protected Object beforeDelete(@NonNull Long id) {
    return userCache.getById(id);
  }

  @Override
  protected void afterDelete(Object obj) {
    userCache.afterDelete((UserExtPo) obj);
  }

  private void afterSaveOrUpdate(@NonNull UserExtPo entity) {
    userRoleService.saveUserRole(entity.getId(), entity.getRoleIds());
    userCache.afterSaveOrUpdate(entity);
    userRoleCache.deleteCache(entity.getId());
  }
}
