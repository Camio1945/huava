package cn.huava.sys.controller;

import cn.huava.common.controller.BaseController;
import cn.huava.sys.mapper.SysUserMapper;
import cn.huava.sys.pojo.po.SysUser;
import cn.huava.sys.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author Camio1945
 */
@Slf4j
@RestController
@RequestMapping("/sys/user")
public class SysUserController extends BaseController<SysUserService, SysUserMapper, SysUser> {

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SysUser> getById(Long id) {
    return super.getById(id);
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SysUser> create(SysUser entity) {
    return super.create(entity);
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SysUser> update(SysUser entity) {
    return super.update(entity);
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<SysUser> patch(SysUser entity, String... fields) {
    return super.patch(entity, fields);
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> delete(Long id) {
    return super.delete(id);
  }
}
