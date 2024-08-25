package cn.huava.sys.validation.role;

import cn.huava.common.util.Fn;
import cn.huava.sys.pojo.po.RolePo;
import cn.huava.sys.service.role.AceRoleService;
import cn.huava.sys.validation.common.BaseValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author Camio1945
 */
public class UniqueRoleNameValidator extends BaseValidator
    implements ConstraintValidator<UniqueRoleName, RolePo> {

  @Override
  public boolean isValid(RolePo rolePo, ConstraintValidatorContext context) {
    String name = rolePo.getName();
    // when name is blank, other validator will do their job
    if (Fn.isBlank(name)) {
      return true;
    }
    boolean isUpdate = basicValidate(rolePo);
    Long id = isUpdate ? rolePo.getId() : null;
    return !Fn.getBean(AceRoleService.class).isNameExists(id, name);
  }
}
