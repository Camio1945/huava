package cn.huava.sys.controller;

import cn.huava.sys.mapper.SysUserMapper;
import cn.huava.sys.pojo.po.SysUser;
import cn.huava.sys.service.SysUserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    String encode = passwordEncoder.encode("123456");
    return  "" + encode;
  }

  @GetMapping("/secured")
  public String secured() {
    return "Hello, Secured";
  }
}
