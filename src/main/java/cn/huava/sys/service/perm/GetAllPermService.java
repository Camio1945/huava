package cn.huava.sys.service.perm;

import cn.huava.common.service.BaseService;
import cn.huava.common.util.Fn;
import cn.huava.sys.mapper.PermMapper;
import cn.huava.sys.pojo.dto.PermDto;
import cn.huava.sys.pojo.po.PermPo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
class GetAllPermService extends BaseService<PermMapper, PermPo> {

  protected List<PermDto> getAllPerm(boolean excludeElement) {
    List<PermPo> perms = getPerms(excludeElement);
    return getTree(perms);
  }

  private List<PermPo> getPerms(boolean excludeElement) {
    LambdaQueryWrapper<PermPo> wrapper =
        Fn.undeletedWrapper(PermPo::getDeleteInfo)
            .ne(excludeElement, PermPo::getType, "E")
            .orderByAsc(PermPo::getSort);
    return list(wrapper);
  }

  private List<PermDto> getTree(List<PermPo> perms) {
    List<PermDto> menu =
        perms.stream()
            .filter(perm -> perm.getPid() == 0)
            .map(perm -> Fn.toBean(perm, PermDto.class))
            .toList();
    for (PermDto permDto : menu) {
      permDto.setChildren(getChildren(permDto.getId(), perms));
    }
    return menu;
  }

  private List<PermDto> getChildren(long pid, List<PermPo> perms) {
    List<PermDto> children =
        perms.stream()
            .filter(perm -> perm.getPid() == pid)
            .map(perm -> Fn.toBean(perm, PermDto.class))
            .toList();
    for (PermDto child : children) {
      child.setChildren(getChildren(child.getId(), perms));
    }
    return children;
  }
}
