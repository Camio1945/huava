package cn.huava.sys.service.sysuserrole;

import cn.huava.common.service.BaseService;
import cn.huava.sys.mapper.SysUserRoleMapper;
import cn.huava.sys.pojo.po.SysUserRolePo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AceSysUserRoleService extends BaseService<SysUserRoleMapper, SysUserRolePo> {}
