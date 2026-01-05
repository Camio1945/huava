package cn.huava.sys.cache;

import static java.util.stream.Collectors.joining;

import cn.huava.common.util.*;
import cn.huava.sys.mapper.UserRoleMapper;
import cn.huava.sys.pojo.po.UserRolePo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

/**
 * 用户拥有的角色缓存
 *
 * @author Camio1945
 */
@Service
@AllArgsConstructor
public class UserRoleCache {
  public static final String ROLE_IDS_BY_USER_ID_CACHE_PREFIX = "cache:userRole:userId";

  private UserRoleMapper userRoleMapper;

  /**
   * 1. Don't use @Cacheable, otherwise will get this error: <br>
   * SerializationException: Could not read JSON:Unexpected token (START_ARRAY), expected
   * VALUE_STRING: need String, Number of Boolean value that contains type id (for subtype of
   * java.lang.Object)
   */
  public @NonNull List<Long> getRoleIdsByUserId(@NonNull Long userId) {
    String key = ROLE_IDS_BY_USER_ID_CACHE_PREFIX + "::" + userId;
    String roleIdsStr = RedisUtil.get(key);
    if (Fn.isBlank(roleIdsStr)) {
      roleIdsStr =
          SingleFlightUtil.execute(
              key,
              () -> {
                LambdaQueryWrapper<UserRolePo> wrapper =
                    new LambdaQueryWrapper<UserRolePo>()
                        .eq(UserRolePo::getUserId, userId)
                        .select(UserRolePo::getRoleId);
                return userRoleMapper.selectList(wrapper).stream()
                    .map(userRolePo -> userRolePo.getRoleId().toString())
                    .collect(joining(","));
              });
      RedisUtil.set(key, roleIdsStr, RedisUtil.randomOffsetDurationInSeconds());
    }
    return Arrays.stream(roleIdsStr.split(",")).map(Long::parseLong).toList();
  }

  public void deleteCache(@NonNull Long userId) {
    RedisUtil.delete(ROLE_IDS_BY_USER_ID_CACHE_PREFIX + "::" + userId);
  }
}
