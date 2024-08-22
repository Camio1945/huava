package cn.huava.sys.service.roleperm;

import cn.huava.common.service.BaseService;
import cn.huava.sys.mapper.RolePermMapper;
import cn.huava.sys.pojo.po.RolePermPo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AceRolePermService extends BaseService<RolePermMapper, RolePermPo> {}
