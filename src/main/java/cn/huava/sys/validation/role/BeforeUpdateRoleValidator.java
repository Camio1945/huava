package cn.huava.sys.validation.role;

import static cn.huava.common.constant.CommonConstant.ADMIN_ROLE_ID;
import static cn.huava.common.constant.CommonConstant.RoleMessage.IMPORTANT_ROLE;

import cn.huava.sys.pojo.po.RolePo;
import cn.huava.common.validation.BaseValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 更新角色前的校验器，与 {@link BeforeUpdateRole} 注解配合使用
 *
 * @author Camio1945
 */
public class BeforeUpdateRoleValidator extends BaseValidator
    implements ConstraintValidator<BeforeUpdateRole, RolePo> {

  @Override
  public boolean isValid(RolePo rolePo, ConstraintValidatorContext context) {
    Long id = rolePo.getId();
    if (id == null) {
      return true;
    }
    if (id == ADMIN_ROLE_ID) {
      return customMessage(context, IMPORTANT_ROLE);
    }
    return true;
  }
}
