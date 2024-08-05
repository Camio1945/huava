package cn.huava.sys.controller;

import cn.huava.common.controller.BaseController;
import cn.huava.sys.mapper.SysUserMapper;
import cn.huava.sys.pojo.po.SysUser;
import cn.huava.sys.service.SysUserService;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author Camio1945
 */
@Slf4j
@RestController
// @AllArgsConstructor
@RequestMapping("/sys/user")
public class SysUserController extends BaseController<SysUserService, SysUserMapper, SysUser> {
  // private SysUserService sysUserService;

  // public SysUserController(ServiceImpl<BaseMapper<SysUser>, SysUser> service) {
  //   super(service);
  // }

  // @GetMapping("/{id}")
  // public SysUser getById(@PathVariable Long id) {
  //   return sysUserService.getById(id);
  // }
  //
  // @PostMapping
  // public boolean add(@RequestBody SysUser sysUser) {
  //   return sysUserService.add(sysUser);
  // }
  //
  // @PatchMapping
  // public boolean update(@RequestBody SysUser sysUser) {
  //   System.out.println("System.out.println 中文测试");
  //   LOGGER.info("LOGGER.info 中文测试");
  //   return sysUserService.update(sysUser);
  // }
  //
  // @DeleteMapping("/{id}")
  // public boolean delete(@PathVariable Long id) {
  //   return sysUserService.delete(id);
  // }
}
