package cn.huava.sys.controller;

import cn.huava.common.controller.BaseController;
import cn.huava.sys.mapper.SysUserMapper;
import cn.huava.sys.pojo.po.SysUser;
import cn.huava.sys.pojo.qo.LoginQo;
import cn.huava.sys.service.sysuser.SysUserAceService;
import java.io.IOException;
import javax.security.auth.login.FailedLoginException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Camio1945
 */
@Slf4j
@RestController
@RequestMapping("/sys/user")
public class SysUserController extends BaseController<SysUserAceService, SysUserMapper, SysUser> {

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody LoginQo loginQo)
      throws IOException, FailedLoginException {
    return ResponseEntity.ok(service.login(loginQo));
  }

  @PostMapping("/refreshToken")
  public ResponseEntity<String> refreshToken(@RequestBody String refreshToken)
      throws IOException, FailedLoginException {
    return ResponseEntity.ok(service.refreshToken(refreshToken));
  }
}
