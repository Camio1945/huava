package cn.huava.sys.service.user;

import static java.util.stream.Collectors.toSet;

import cn.huava.common.constant.CommonConstant;
import cn.huava.common.service.BaseService;
import cn.huava.common.util.Fn;
import cn.huava.sys.cache.UserRoleCache;
import cn.huava.sys.enumeration.PermTypeEnum;
import cn.huava.sys.mapper.UserMapper;
import cn.huava.sys.pojo.dto.PermDto;
import cn.huava.sys.pojo.dto.UserInfoDto;
import cn.huava.sys.pojo.po.*;
import cn.huava.sys.service.perm.AcePermService;
import cn.huava.sys.service.roleperm.AceRolePermService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
class GetUserInfoService extends BaseService<UserMapper, UserExtPo> {
  private final AceRolePermService rolePermService;
  private final AcePermService permService;
  private final UserRoleCache userRoleCache;

  private static List<String> buildUris(List<PermPo> perms, boolean isAdmin, Set<Long> permIds) {
    if (isAdmin) {
      return List.of("*");
    }
    return perms.stream()
        .filter(perm -> Fn.isNotBlank(perm.getUri()))
        .filter(perm -> permIds.contains(perm.getId()))
        .map(PermPo::getUri)
        .collect(toSet())
        .stream()
        .sorted()
        .toList();
  }

  protected UserInfoDto getUserInfoDto() {
    UserInfoDto userInfoDto = new UserInfoDto();
    UserPo loginUser = Fn.getLoginUser();
    setBaseInfo(loginUser, userInfoDto);
    setMenuAndPerms(loginUser, userInfoDto);
    return userInfoDto;
  }

  private void setBaseInfo(UserPo loginUser, UserInfoDto dto) {
    dto.setUsername(loginUser.getUsername());
    String avatar = loginUser.getAvatar();
    if (Fn.isNotBlank(avatar)) {
      HttpServletRequest req = Fn.getRequest();
      avatar =
          Fn.format(
              "{}://{}:{}{}", req.getScheme(), req.getServerName(), req.getServerPort(), avatar);
      dto.setAvatar(avatar);
    }
  }

  private void setMenuAndPerms(UserPo loginUser, UserInfoDto userInfoDto) {
    List<PermPo> perms = getPerms();
    boolean isAdmin = loginUser.getId() == CommonConstant.ADMIN_USER_ID;
    final Set<Long> permIds = getPermIds(isAdmin, loginUser);
    userInfoDto.setMenu(buildMenuTree(perms, isAdmin, permIds));
    userInfoDto.setUris(buildUris(perms, isAdmin, permIds));
  }

  /**
   * build the perms to tree, they are the menus that user can see on the left panel in their
   * browser
   */
  private List<PermDto> buildMenuTree(List<PermPo> perms, boolean isAdmin, Set<Long> permIds) {
    List<PermDto> menu =
        perms.stream()
            .filter(perm -> perm.getPid() == 0)
            .filter(perm -> !perm.getType().equals(PermTypeEnum.E.name()))
            .filter(perm -> isAdmin || permIds.contains(perm.getId()))
            .map(perm -> Fn.toBean(perm, PermDto.class))
            .toList();
    for (PermDto permDto : menu) {
      permDto.setChildren(getChildren(permDto.getId(), perms, isAdmin, permIds));
    }
    return menu;
  }

  /** Get all the perm ids the user have */
  private Set<Long> getPermIds(boolean isAdmin, UserPo loginUser) {
    final Set<Long> permIds = new HashSet<>();
    if (!isAdmin) {
      List<Long> roleIds = userRoleCache.getRoleIdsByUserId(loginUser.getId());
      Wrapper<RolePermPo> wrapper =
          new LambdaQueryWrapper<RolePermPo>()
              .in(RolePermPo::getRoleId, roleIds)
              .select(RolePermPo::getPermId);
      Set<Long> permIdSet =
          rolePermService.list(wrapper).stream().map(RolePermPo::getPermId).collect(toSet());
      permIds.addAll(permIdSet);
    }
    return permIds;
  }

  /** Get enabled perms, (just directories and menus, without elements) */
  private List<PermPo> getPerms() {
    LambdaQueryWrapper<PermPo> wrapper =
        Fn.undeletedWrapper(PermPo::getDeleteInfo)
            .eq(PermPo::getIsEnabled, true)
            .orderByAsc(PermPo::getSort);
    return permService.list(wrapper);
  }

  private List<PermDto> getChildren(
      long pid, List<PermPo> perms, boolean isAdmin, Set<Long> permIds) {
    List<PermDto> children =
        perms.stream()
            .filter(perm -> perm.getPid() == pid)
            .filter(perm -> !perm.getType().equals(PermTypeEnum.E.name()))
            .filter(perm -> isAdmin || permIds.contains(perm.getId()))
            .map(perm -> Fn.toBean(perm, PermDto.class))
            .toList();
    for (PermDto child : children) {
      child.setChildren(getChildren(child.getId(), perms, isAdmin, permIds));
    }
    return children;
  }
}
