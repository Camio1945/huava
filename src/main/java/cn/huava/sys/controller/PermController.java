package cn.huava.sys.controller;

import cn.huava.common.controller.BaseController;
import cn.huava.sys.mapper.PermMapper;
import cn.huava.sys.pojo.po.PermPo;
import cn.huava.sys.service.perm.AcePermService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author Camio1945
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sys/perm")
public class PermController extends BaseController<AcePermService, PermMapper, PermPo> {}
