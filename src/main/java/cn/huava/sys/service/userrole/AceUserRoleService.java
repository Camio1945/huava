package cn.huava.sys.service.userrole;

import cn.huava.common.service.BaseService;
import cn.huava.sys.mapper.UserRoleMapper;
import cn.huava.sys.pojo.po.UserRolePo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AceUserRoleService extends BaseService<UserRoleMapper, UserRolePo> {}
