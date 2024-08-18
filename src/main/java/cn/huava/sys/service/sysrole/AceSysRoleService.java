package cn.huava.sys.service.sysrole;

import cn.huava.common.service.BaseService;
import cn.huava.sys.mapper.SysRoleMapper;
import cn.huava.sys.pojo.po.SysRolePo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AceSysRoleService extends BaseService<SysRoleMapper, SysRolePo> {}
