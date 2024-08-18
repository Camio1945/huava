package cn.huava.sys.controller;

import cn.huava.common.controller.BaseController;
import cn.huava.sys.mapper.SysPermMapper;
import cn.huava.sys.pojo.po.SysPermPo;
import cn.huava.sys.service.sysperm.AceSysPermService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author Camio1945
 */
@Slf4j
@RestController
@RequestMapping("/sys/perm")
public class SysPermController
    extends BaseController<AceSysPermService, SysPermMapper, SysPermPo> {}
