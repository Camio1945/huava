package cn.huava.sys.service.perm;

import cn.huava.common.service.BaseService;
import cn.huava.sys.mapper.PermMapper;
import cn.huava.sys.pojo.po.PermPo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AcePermService extends BaseService<PermMapper, PermPo> {}
