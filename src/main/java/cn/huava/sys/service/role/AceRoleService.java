package cn.huava.sys.service.role;

import cn.huava.common.pojo.dto.PageDto;
import cn.huava.common.pojo.qo.PageQo;
import cn.huava.common.service.BaseService;
import cn.huava.common.util.Fn;
import cn.huava.sys.cache.RoleCache;
import cn.huava.sys.mapper.RoleMapper;
import cn.huava.sys.pojo.po.*;
import cn.huava.sys.pojo.qo.SetPermQo;
import cn.huava.sys.service.roleperm.AceRolePermService;
import cn.huava.sys.service.userrole.AceUserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AceRoleService extends BaseService<RoleMapper, RolePo> {
  private final AceUserRoleService userRoleService;
  private final RolePageService rolePageService;
  private final AceRolePermService rolePermService;
  private final RoleCache roleCache;

  public List<String> getRoleNamesByUserId(@NonNull final Long userId) {
    List<UserRolePo> userRoles =
        userRoleService.list(
            new LambdaQueryWrapper<UserRolePo>().eq(UserRolePo::getUserId, userId));
    List<Long> roleIds = userRoles.stream().map(UserRolePo::getRoleId).toList();
    List<RolePo> roles = listByIds(roleIds);
    return roles.stream().map(RolePo::getName).toList();
  }

  public PageDto<RolePo> rolePage(@NonNull PageQo<RolePo> pageQo, @NonNull final RolePo params) {
    return rolePageService.rolePage(pageQo, params);
  }

  public boolean isNameExists(Long id, @NonNull String name) {
    return exists(
        Fn.undeletedWrapper(RolePo::getDeleteInfo)
            .eq(RolePo::getName, name)
            .ne(id != null, RolePo::getId, id));
  }

  @Transactional(rollbackFor = Throwable.class)
  public void setPerm(@NonNull SetPermQo setPermQo) {
    Long roleId = setPermQo.getRoleId();
    rolePermService.remove(new LambdaQueryWrapper<RolePermPo>().eq(RolePermPo::getRoleId, roleId));
    List<Long> permIds = setPermQo.getPermIds();
    if (CollUtil.isNotEmpty(permIds)) {
      List<RolePermPo> rolePermPos =
          permIds.stream().map(permId -> new RolePermPo(roleId, permId)).toList();
      rolePermService.saveBatch(rolePermPos);
    }
    roleCache.deleteCache(roleId);
  }

  public List<Long> getPerm(@NonNull Long id) {
    return rolePermService
        .list(new LambdaQueryWrapper<RolePermPo>().eq(RolePermPo::getRoleId, id))
        .stream()
        .map(RolePermPo::getPermId)
        .toList();
  }
}
