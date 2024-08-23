package cn.huava.sys.controller;

import cn.huava.common.controller.BaseController;
import cn.huava.common.util.Fn;
import cn.huava.sys.mapper.UserMapper;
import cn.huava.sys.pojo.dto.UserInfoDto;
import cn.huava.sys.pojo.dto.UserJwtDto;
import cn.huava.sys.pojo.po.UserPo;
import cn.huava.sys.pojo.qo.LoginQo;
import cn.huava.sys.service.role.AceRoleService;
import cn.huava.sys.service.user.AceUserService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Camio1945
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sys/user")
public class UserController extends BaseController<AceUserService, UserMapper, UserPo> {
  private final AceRoleService roleService;

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
}
