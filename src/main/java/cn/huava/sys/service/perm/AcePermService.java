package cn.huava.sys.service.perm;

import cn.huava.common.service.BaseService;
import cn.huava.sys.mapper.PermMapper;
import cn.huava.sys.pojo.dto.PermDto;
import cn.huava.sys.pojo.po.PermPo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 权限服务主入口类<br>
 *
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AcePermService extends BaseService<PermMapper, PermPo> {
  private final GetAllPermService getAllPermService;

  public List<PermDto> getAllPerm(boolean excludeElement) {
    return getAllPermService.getAllPerm(excludeElement);
  }
}
