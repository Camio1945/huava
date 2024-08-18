package cn.huava.sys.controller;

import cn.huava.common.controller.BaseController;
import cn.huava.sys.mapper.SysRoleMapper;
import cn.huava.sys.pojo.po.SysRolePo;
import cn.huava.sys.service.sysrole.AceSysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author Camio1945
 */
@Slf4j
@RestController
@RequestMapping("/sys/role")
public class SysRoleController
    extends BaseController<AceSysRoleService, SysRoleMapper, SysRolePo> {}
