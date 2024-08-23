package cn.huava.sys.validation.role;

import static cn.huava.common.constant.CommonConstant.ADMIN_ROLE_ID;
import static cn.huava.common.constant.CommonConstant.RoleMessage.IMPORTANT_ROLE;

import cn.huava.sys.pojo.po.RolePo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author Camio1945
 */
public class BeforeUpdateRoleValidator implements ConstraintValidator<BeforeUpdateRole, RolePo> {

  @Override
  public boolean isValid(RolePo rolePo, ConstraintValidatorContext context) {
    Long id = rolePo.getId();
    if (id == null) {
      return true;
    }
    if (id == ADMIN_ROLE_ID) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(IMPORTANT_ROLE).addConstraintViolation();
      return false;
    }
    return true;
  }
}
