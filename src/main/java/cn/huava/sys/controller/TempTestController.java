package cn.huava.sys.controller;

import cn.huava.sys.service.sysuser.SysUserAceService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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

  // private PasswordEncoder passwordEncoder;

  @GetMapping("/")
  public String test() {
    // String encode = passwordEncoder.encode("123456");
    // return  "" + encode;
    return "";
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
