package cn.huava.sys.service.user;

import static cn.huava.sys.service.user.UserServiceUtil.buildMenuTree;
import static java.util.stream.Collectors.toSet;

import cn.huava.common.constant.CommonConstant;
import cn.huava.common.service.BaseService;
import cn.huava.common.util.Fn;
import cn.huava.sys.cache.UserRoleCache;
import cn.huava.sys.mapper.UserMapper;
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
 * 获取用户信息
 *
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
class GetUserInfoService extends BaseService<UserMapper, UserExtPo> {
  private final AceRolePermService rolePermService;
  private final AcePermService permService;
  private final UserRoleCache userRoleCache;

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
    // 根据用户 ID 或用户所属的角色 ID 来判断是否属于超级管理员
    boolean isAdminRole = loginUser.getId() == CommonConstant.ADMIN_USER_ID;
    List<Long> roleIds = null;
    if (!isAdminRole) {
      roleIds = userRoleCache.getRoleIdsByUserId(loginUser.getId());
      isAdminRole = roleIds.contains(CommonConstant.ADMIN_ROLE_ID);
    }
    final Set<Long> permIds = getPermIds(isAdminRole, roleIds);
    userInfoDto.setMenu(buildMenuTree(perms, isAdminRole, permIds));
    userInfoDto.setUris(buildUris(perms, isAdminRole, permIds));
  }

  /** Get enabled perms, (just directories and menus, without elements) */
  private List<PermPo> getPerms() {
    LambdaQueryWrapper<PermPo> wrapper =
        Fn.undeletedWrapper(PermPo::getDeleteInfo)
            .eq(PermPo::getIsEnabled, true)
            .orderByAsc(PermPo::getSort);
    return permService.list(wrapper);
  }

  /** Get all the perm ids the user have */
  private Set<Long> getPermIds(boolean isAdminRole, List<Long> roleIds) {
    final Set<Long> permIds = new HashSet<>();
    if (!isAdminRole) {
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

  private static List<String> buildUris(
      List<PermPo> perms, boolean isAdminRole, Set<Long> permIds) {
    if (isAdminRole) {
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
}
