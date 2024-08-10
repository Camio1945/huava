package cn.huava.sys.controller;

import cn.huava.sys.pojo.dto.SysUserJwtDto;
import cn.huava.sys.pojo.query.LoginQuery;
import cn.huava.sys.service.SysUserJwtAuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Camio1945
 */
@AllArgsConstructor
@RestController
@RequestMapping("/sysUserJwtAuth")
public class SysUserJwtAuthController {

  private final SysUserJwtAuthService sysUserJwtAuthService;

  @PostMapping("/login")
  public ResponseEntity<SysUserJwtDto> login(@RequestBody LoginQuery loginQuery) {
    String token = sysUserJwtAuthService.login(loginQuery);
    SysUserJwtDto sysUserJwtDto = new SysUserJwtDto(token);
    return new ResponseEntity<>(sysUserJwtDto, HttpStatus.OK);
  }
}
