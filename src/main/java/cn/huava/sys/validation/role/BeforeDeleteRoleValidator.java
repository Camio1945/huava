package cn.huava.sys.validation.role;

import static cn.huava.common.constant.CommonConstant.ADMIN_ROLE_ID;
import static cn.huava.common.constant.CommonConstant.RoleMessage.IMPORTANT_ROLE;

import cn.huava.common.util.Fn;
import cn.huava.sys.pojo.po.RolePo;
import cn.huava.sys.service.userrole.AceUserRoleService;
import cn.huava.common.validation.BaseValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 删除角色前的校验器，与 {@link BeforeDeleteRole} 注解配合使用
 *
 * @author Camio1945
 */
public class BeforeDeleteRoleValidator extends BaseValidator
    implements ConstraintValidator<BeforeDeleteRole, RolePo> {

  @Override
  public boolean isValid(RolePo rolePo, ConstraintValidatorContext context) {
    Long id = rolePo.getId();
    if (id == null) {
      return customMessage(context, "角色ID不能为空");
    }
    if (id == ADMIN_ROLE_ID) {
      return customMessage(context, IMPORTANT_ROLE);
    }
    AceUserRoleService userRoleService = Fn.getBean(AceUserRoleService.class);
    long userCount = userRoleService.countUserByRoleId(id);
    if (userCount > 0) {
      return customMessage(context, "角色下存在用户，不能删除");
    }
    return true;
  }
}
