package cn.huava.sys.controller;

import cn.huava.sys.service.SysUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
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
  private SysUserService sysUserService;

  private PasswordEncoder passwordEncoder;

  @GetMapping("/")
  public String test() {
    String encode = passwordEncoder.encode("password");
    return  "" + encode;
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
