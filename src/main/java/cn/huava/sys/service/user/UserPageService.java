package cn.huava.sys.service.user;

import static java.util.stream.Collectors.*;

import cn.huava.common.pojo.dto.PageDto;
import cn.huava.common.pojo.qo.PageQo;
import cn.huava.common.service.BaseService;
import cn.huava.common.util.Fn;
import cn.huava.sys.mapper.UserMapper;
import cn.huava.sys.pojo.dto.UserDto;
import cn.huava.sys.pojo.po.*;
import cn.huava.sys.service.userrole.AceUserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;

/**
 * 用户分页查询
 *
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
class UserPageService extends BaseService<UserMapper, UserExtPo> {
  private final AceUserRoleService userRoleService;

  protected PageDto<UserDto> userPage(
      @NonNull PageQo<UserExtPo> pageQo, @NonNull final UserExtPo params) {
    pageQo = getPageQo(pageQo, params);
    List<UserDto> userDtos = pageQo.getRecords().stream().map(UserDto::new).toList();
    setRoleIds(userDtos);
    return new PageDto<>(userDtos, pageQo.getTotal());
  }

  private static Map<Long, List<Long>> getUserIdToRoleIdsMap(List<UserRolePo> userRoles) {
    return userRoles.stream()
        .collect(groupingBy(UserRolePo::getUserId, mapping(UserRolePo::getRoleId, toList())));
  }

  /** Note: the `params` renamed to po to save some space */
  private PageQo<UserExtPo> getPageQo(PageQo<UserExtPo> pageQo, UserExtPo po) {
    LambdaQueryWrapper<UserExtPo> wrapper = Fn.undeletedWrapper(UserExtPo::getDeleteInfo);
    wrapper
        .eq(Fn.isNotBlank(po.getUsername()), UserExtPo::getUsername, po.getUsername())
        .like(Fn.isNotBlank(po.getRealName()), UserExtPo::getRealName, po.getRealName())
        .eq(Fn.isNotBlank(po.getPhoneNumber()), UserExtPo::getPhoneNumber, po.getPhoneNumber())
        .orderByDesc(UserExtPo::getId);
    pageQo = page(pageQo, wrapper);
    return pageQo;
  }

  private void setRoleIds(List<UserDto> userDtos) {
    if (CollUtil.isEmpty(userDtos)) {
      return;
    }
    List<UserRolePo> userRoles = getUserRoles(userDtos);
    Map<Long, List<Long>> userIdToRoleIdsMap = getUserIdToRoleIdsMap(userRoles);
    for (UserDto userDto : userDtos) {
      List<Long> roleIds = userIdToRoleIdsMap.get(userDto.getId());
      userDto.setRoleIds(roleIds);
    }
  }

  private List<UserRolePo> getUserRoles(List<UserDto> userDtos) {
    List<Long> userIds = userDtos.stream().map(UserDto::getId).toList();
    LambdaUpdateWrapper<UserRolePo> wrapper = new LambdaUpdateWrapper<>();
    return userRoleService.list(wrapper.in(UserRolePo::getUserId, userIds));
  }
}
