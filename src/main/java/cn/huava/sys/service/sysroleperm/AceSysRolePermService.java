package cn.huava.sys.service.sysroleperm;

import cn.huava.common.service.BaseService;
import cn.huava.sys.mapper.SysRolePermMapper;
import cn.huava.sys.pojo.po.SysRolePermPo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AceSysRolePermService extends BaseService<SysRolePermMapper, SysRolePermPo> {}
