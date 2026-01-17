package cn.huava.sys.cache;

import static java.util.stream.Collectors.toSet;

import cn.huava.common.util.RedisUtil;
import cn.huava.common.util.SingleFlightUtil;
import cn.huava.sys.mapper.*;
import cn.huava.sys.pojo.po.PermPo;
import cn.huava.sys.pojo.po.RolePermPo;
import cn.hutool.v7.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 角色缓存
 *
 * @author Camio1945
 */
@Service
@NullMarked
@AllArgsConstructor
public class RoleCache {
  public static final String URIS_CACHE_PREFIX = "cache:role:uris:roleId";

  private RolePermMapper rolePermMapper;
  private PermMapper permMapper;

  @Cacheable(value = URIS_CACHE_PREFIX, key = "#roleId", unless = "#result == null")
  public Set<String> getPermUrisByRoleId(Long roleId) {
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
    return permMapper.selectByIds(permIds).stream().map(PermPo::getUri).collect(toSet());
  }

  public void deleteCache(Long roleId) {
    RedisUtil.delete(URIS_CACHE_PREFIX + "::" + roleId);
  }
}
