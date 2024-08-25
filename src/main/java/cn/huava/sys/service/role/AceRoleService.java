package cn.huava.sys.service.role;

import cn.huava.common.pojo.dto.PageDto;
import cn.huava.common.pojo.qo.PageQo;
import cn.huava.common.service.BaseService;
import cn.huava.common.util.Fn;
import cn.huava.sys.mapper.RoleMapper;
import cn.huava.sys.pojo.po.RolePo;
import cn.huava.sys.pojo.po.UserRolePo;
import cn.huava.sys.service.userrole.AceUserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AceRoleService extends BaseService<RoleMapper, RolePo> {
  private final AceUserRoleService userRoleService;
  private final RolePageService rolePageService;

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
        Fn.buildUndeletedWrapper(RolePo::getDeleteInfo)
            .eq(RolePo::getName, name)
            .ne(id != null, RolePo::getId, id));
  }
}
