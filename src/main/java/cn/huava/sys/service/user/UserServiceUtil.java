package cn.huava.sys.service.user;

import cn.huava.common.annotation.VisibleForTesting;
import cn.huava.common.enumeration.AccessModifierEnum;
import cn.huava.common.util.Fn;
import cn.huava.sys.enumeration.PermTypeEnum;
import cn.huava.sys.pojo.dto.PermDto;
import cn.huava.sys.pojo.po.PermPo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;

/**
 * 用户模块的工具类（把一些公共的或者不方便测试的方法移到这个类中，以复用或方便测试）
 *
 * @author Camio1945
 * @since 2026-01-17
 */
@Slf4j
@VisibleForTesting(original = AccessModifierEnum.PRIVATE)
public class UserServiceUtil {
  private UserServiceUtil() {}

  /**
   * build the perms to tree, they are the menus that user can see on the left panel in their
   * browser
   */
  protected static List<PermDto> buildMenuTree(List<PermPo> perms, boolean isAdminRole, Set<Long> permIds) {
    List<PermDto> menu =
      perms.stream()
        .filter(perm -> perm.getPid() == 0)
        .filter(perm -> !perm.getType().equals(PermTypeEnum.E.name()))
        .filter(perm -> isAdminRole || permIds.contains(perm.getId()))
        .map(perm -> Fn.toBean(perm, PermDto.class))
        .toList();
    for (PermDto permDto : menu) {
      permDto.setChildren(getChildren(permDto.getId(), perms, isAdminRole, permIds));
    }
    return menu;
  }

  protected static List<PermDto> getChildren(
    long pid, List<PermPo> perms, boolean isAdminRole, Set<Long> permIds) {
    List<PermDto> children =
      perms.stream()
        .filter(perm -> perm.getPid() == pid)
        .filter(perm -> !perm.getType().equals(PermTypeEnum.E.name()))
        .filter(perm -> isAdminRole || permIds.contains(perm.getId()))
        .map(perm -> Fn.toBean(perm, PermDto.class))
        .toList();
    for (PermDto child : children) {
      child.setChildren(getChildren(child.getId(), perms, isAdminRole, permIds));
    }
    return children;
  }
}
