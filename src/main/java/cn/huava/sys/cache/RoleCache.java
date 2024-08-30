package cn.huava.sys.cache;

import static java.util.stream.Collectors.toSet;

import cn.huava.common.util.RedisUtil;
import cn.huava.common.util.SingleFlightUtil;
import cn.huava.sys.mapper.*;
import cn.huava.sys.pojo.po.PermPo;
import cn.huava.sys.pojo.po.RolePermPo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.Collections;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.dromara.hutool.core.collection.CollUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 角色缓存
 *
 * @author Camio1945
 */
@Service
@AllArgsConstructor
public class RoleCache {
  public static final String URIS_CACHE_PREFIX = "cache:role:uris:roleId";

  private RolePermMapper rolePermMapper;
  private PermMapper permMapper;

  @Cacheable(value = URIS_CACHE_PREFIX, key = "#roleId")
  public Set<String> getPermUrisByRoleId(@NonNull Long roleId) {
    String key = URIS_CACHE_PREFIX + "::" + roleId;
    return SingleFlightUtil.execute(key, () -> getPermUrisByRoleIdInner(roleId));
  }

  private Set<String> getPermUrisByRoleIdInner(Long roleId) {
    LambdaQueryWrapper<RolePermPo> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(RolePermPo::getRoleId, roleId).select(RolePermPo::getPermId);
    Set<Long> permIds =
        rolePermMapper.selectList(wrapper).stream().map(RolePermPo::getPermId).collect(toSet());
    if (CollUtil.isEmpty(permIds)) {
      return Collections.emptySet();
    }
    return permMapper.selectBatchIds(permIds).stream().map(PermPo::getUri).collect(toSet());
  }

  public void deleteCache(@NonNull Long roleId) {
    RedisUtil.delete(URIS_CACHE_PREFIX + "::" + roleId);
  }
}
