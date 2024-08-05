package cn.huava.sys.controller;

import cn.huava.sys.mapper.SysUserMapper;
import cn.huava.sys.pojo.po.SysUser;
import cn.huava.sys.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 临时测试控制器
 *
 * @author Camio1945
 */
@RestController
@RequestMapping("/temp/test")
public class TempTestController {
  @Autowired private SysUserService sysUserService;

  @GetMapping
  public String test() {
    return sysUserService.test().getUserName();
  }
}
