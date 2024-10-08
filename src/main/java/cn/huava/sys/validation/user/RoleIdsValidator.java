package cn.huava.sys.validation.user;

import cn.huava.common.util.Fn;
import cn.huava.sys.pojo.po.RolePo;
import cn.huava.sys.service.role.AceRoleService;
import cn.huava.common.validation.BaseValidator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import org.dromara.hutool.core.collection.CollUtil;

/**
 * 用户所拥有的角色 ids 校验器，与 {@link RoleIds} 注解配合使用
 *
 * @author Camio1945
 */
public class RoleIdsValidator extends BaseValidator
    implements ConstraintValidator<RoleIds, List<Long>> {

  @Override
  public boolean isValid(List<Long> roleIds, ConstraintValidatorContext context) {
    if (CollUtil.isEmpty(roleIds)) {
      return customMessage(context, "角色不能为空");
    }
    AceRoleService service = Fn.getBean(AceRoleService.class);
    long dbCount = service.count(new LambdaQueryWrapper<RolePo>().in(RolePo::getId, roleIds));
    if (roleIds.size() != dbCount) {
      return customMessage(context, "角色不存在");
    }
    return true;
  }
}
