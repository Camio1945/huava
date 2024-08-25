package cn.huava.sys.service.userrole;

import cn.huava.common.service.BaseService;
import cn.huava.common.util.Fn;
import cn.huava.sys.mapper.UserRoleMapper;
import cn.huava.sys.pojo.po.UserRolePo;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Camio1945
 */
@Slf4j
@Service
public class AceUserRoleService extends BaseService<UserRoleMapper, UserRolePo> {
  public void saveUserRole(@NonNull final Long userId, @NonNull final List<Long> roleIds) {
    AceUserRoleService self = getSelf();
    self.remove(new LambdaQueryWrapper<UserRolePo>().eq(UserRolePo::getUserId, userId));
    List<UserRolePo> userRolePos =
        roleIds.stream()
            .map(roleId -> new UserRolePo().setUserId(userId).setRoleId(roleId))
            .toList();
    self.saveBatch(userRolePos);
  }

  /**
   * Note 1: It is necessary to use `self.saveBatch(userRolePos)` other than just
   * `saveBatch(userRolePos)`, see <a
   * href="https://cloud-ci.sgs.com/sonar/coding_rules?open=java%3AS6809&rule_key=java%3AS6809">
   * java:S6809</a> <br>
   * <br>
   * Note 2: The `self` cannot be injected by constructor (like the code below), otherwise, it will
   * cause an exception in GraalVM native image: CglibAopProxy$SerializableNoOp cannot be cast to
   * org.springframework.cglib.proxy.Dispatcher
   *
   * <pre>
   * private final AceUserRoleService self;
   * public AceUserRoleService(@Lazy AceUserRoleService self) {
   *   this.self = self;
   * }
   * </pre>
   */
  public AceUserRoleService getSelf() {
    return Fn.getBean(AceUserRoleService.class);
  }

  public List<Long> getRoleIdsByUserId(@NonNull final Long userId) {
    Wrapper<UserRolePo> wrapper =
        new LambdaQueryWrapper<UserRolePo>()
            .eq(UserRolePo::getUserId, userId)
            .select(UserRolePo::getRoleId);
    return list(wrapper).stream().map(UserRolePo::getRoleId).toList();
  }
}
