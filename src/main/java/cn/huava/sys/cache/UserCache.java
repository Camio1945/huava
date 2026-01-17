package cn.huava.sys.cache;

import cn.huava.common.util.*;
import cn.huava.sys.mapper.UserMapper;
import cn.huava.sys.pojo.po.UserExtPo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 用户缓存
 *
 * @author Camio1945
 */
@Service
@NullMarked
@RequiredArgsConstructor
public class UserCache {
  public static final String USER_ID_CACHE_PREFIX = "cache:user:id";
  public static final String USER_USERNAME_CACHE_PREFIX = "cache:user:username";

  private final UserMapper userMapper;

  /**
   * 1. Don't add `final` key word, otherwise will get circular dependency error. <br>
   * 2. Don't use it directly, use {@link #getUserCacheInner()} instead. <br>
   * 3. Don't delete it and use `this`, otherwise the cache will not work. <br>
   */
  private @Nullable UserCache userCacheInner;

  /**
   * 根据 id 获取用户
   *
   * @param id 用户 id
   * @return 用户
   */
  @Cacheable(value = USER_ID_CACHE_PREFIX, key = "#id", unless = "#result == null")
  public UserExtPo getById(Long id) {
    String key = USER_ID_CACHE_PREFIX + "::" + id;
    return SingleFlightUtil.execute(key, () -> userMapper.selectById(id));
  }

  /**
   * 根据用户名获取用户 id
   *
   * @param username 用户名
   * @return 用户 id
   */
  public @Nullable Long getIdByUsername(String username) {
    String strId = getUserCacheInner().getStrIdByUsername(username);
    return strId == null ? null : Long.parseLong(strId);
  }

  /**
   * 根据用户名获取用户 id
   *
   * @param username 用户名
   * @return 用户 id , has to be type String, not Long, otherwise will get an Exception: <br>
   *     java.lang.ClassCastException: class java.lang.Integer cannot be cast to class
   *     java.lang.Long
   */
  @Cacheable(value = USER_USERNAME_CACHE_PREFIX, key = "#username", unless = "#result == null")
  public @Nullable String getStrIdByUsername(String username) {
    String key = USER_USERNAME_CACHE_PREFIX + "::" + username;
    return SingleFlightUtil.execute(
        key,
        () -> {
          LambdaQueryWrapper<UserExtPo> wrapper = Fn.undeletedWrapper(UserExtPo::getDeleteInfo);
          UserExtPo userExtPo =
              userMapper.selectOne(
                  wrapper.eq(UserExtPo::getUsername, username).select(UserExtPo::getId));
          return userExtPo == null ? null : userExtPo.getId().toString();
        });
  }

  private UserCache getUserCacheInner() {
    if (userCacheInner != null) {
      return userCacheInner;
    }
    synchronized (UserCache.class) {
      userCacheInner = Fn.getBean(UserCache.class);
    }
    return userCacheInner;
  }

  /**
   * 新增或修改操作后的缓存处理
   *
   * @param after 已经保存到数据库之后的用户
   */
  public void afterSaveOrUpdate(UserExtPo after) {
    deleteKeys(after);
  }

  private void deleteKeys(UserExtPo user) {
    String[] keys = {
      USER_ID_CACHE_PREFIX + "::" + user.getId(),
      USER_USERNAME_CACHE_PREFIX + "::" + user.getUsername()
    };
    RedisUtil.delete(keys);
  }

  /**
   * 删除操作后的缓存处理
   *
   * @param before 删除前的用户
   */
  public void afterDelete(UserExtPo before) {
    deleteKeys(before);
  }

  /**
   * 更新操作前的缓存处理
   *
   * @param before 更新前的用户
   */
  public void beforeUpdate(UserExtPo before) {
    RedisUtil.delete(USER_USERNAME_CACHE_PREFIX + "::" + before.getUsername());
  }
}
