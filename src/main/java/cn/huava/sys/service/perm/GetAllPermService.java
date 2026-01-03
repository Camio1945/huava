package cn.huava.sys.service.perm;

import static cn.huava.common.util.MybatisPlusTool.undeletedWrapper;
import static cn.hutool.v7.core.bean.BeanUtil.toBean;

import cn.huava.common.service.BaseService;
import cn.huava.sys.mapper.PermMapper;
import cn.huava.sys.pojo.dto.PermDto;
import cn.huava.sys.pojo.po.PermPo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 获取所有的权限
 *
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
class GetAllPermService extends BaseService<PermMapper, PermPo> {

  protected List<PermDto> getAllPerm(boolean isElementExcluded) {
    List<PermPo> perms = getPerms(isElementExcluded);
    return getTree(perms);
  }

  private List<PermPo> getPerms(boolean isElementExcluded) {
    LambdaQueryWrapper<PermPo> wrapper =
        undeletedWrapper(PermPo::getDeleteInfo)
            .ne(isElementExcluded, PermPo::getType, "E")
            .orderByAsc(PermPo::getSort);
    return list(wrapper);
  }

  private List<PermDto> getTree(List<PermPo> perms) {
    List<PermDto> menu =
        perms.stream()
            .filter(perm -> perm.getPid() == 0)
            .map(perm -> toBean(perm, PermDto.class))
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
            .map(perm -> toBean(perm, PermDto.class))
            .toList();
    for (PermDto child : children) {
      child.setChildren(getChildren(child.getId(), perms));
    }
    return children;
  }
}
