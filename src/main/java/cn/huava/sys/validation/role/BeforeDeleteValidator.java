package cn.huava.sys.validation.role;

import static cn.huava.common.constant.CommonConstant.ADMIN_ROLE_ID;

import cn.huava.common.util.Fn;
import cn.huava.sys.pojo.po.RolePo;
import cn.huava.sys.pojo.po.UserRolePo;
import cn.huava.sys.service.userrole.AceUserRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author Camio1945
 */
public class BeforeDeleteValidator implements ConstraintValidator<BeforeDelete, RolePo> {

  @Override
  public boolean isValid(RolePo rolePo, ConstraintValidatorContext context) {
    Long id = rolePo.getId();
    if (id == null) {
      return customMessage(context, "角色ID不能为空");
    }
    if (id == ADMIN_ROLE_ID) {
      return customMessage(context, "该角色为最重要的基础角色，不允许进行任何操作");
    }
    AceUserRoleService userRoleService = Fn.getBean(AceUserRoleService.class);
    long userCount =
        userRoleService.count(new LambdaQueryWrapper<UserRolePo>().eq(UserRolePo::getRoleId, id));
    if (userCount > 0) {
      return customMessage(context, "角色下存在用户，不能删除");
    }
    return true;
  }

  private static boolean customMessage(ConstraintValidatorContext context, String messageTemplate) {
    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(messageTemplate).addConstraintViolation();
    return false;
  }
}
