package cn.huava.sys.controller;

import cn.huava.sys.pojo.po.Oauth2AuthorizationPo;
import cn.huava.sys.service.oauth2authorization.Oauth2AuthorizationAceService;
import cn.huava.sys.service.sysuser.SysUserAceService;
import lombok.AllArgsConstructor;
import org.dromara.hutool.json.JSONUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.Oauth2AuthorizationConverter;
import org.springframework.web.bind.annotation.*;

/**
 * 临时测试控制器
 *
 * @author Camio1945
 */
@RestController
@AllArgsConstructor
@RequestMapping("/temp/test")
public class TempTestController {
  private SysUserAceService sysUserService;
  private Oauth2AuthorizationAceService oauth2AuthorizationAceService;

  private PasswordEncoder passwordEncoder;

  @GetMapping("/")
  public String test() {
    Oauth2AuthorizationPo po = oauth2AuthorizationAceService.getById("35e2a8ce-e2c2-4a04-87dc-1b393c0346f4");
    return JSONUtil.toJsonPrettyStr(new String(po.getAttributes()));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/securedAdmin")
  public String securedAdmin() {
    return "Hello, Secured Admin!";
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/securedUser")
  public String securedUser() {
    return "Hello, Secured User!";
  }
}
