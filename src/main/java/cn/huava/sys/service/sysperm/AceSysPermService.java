package cn.huava.sys.service.sysperm;

import cn.huava.common.service.BaseService;
import cn.huava.sys.mapper.SysPermMapper;
import cn.huava.sys.pojo.po.SysPermPo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AceSysPermService extends BaseService<SysPermMapper, SysPermPo> {}
