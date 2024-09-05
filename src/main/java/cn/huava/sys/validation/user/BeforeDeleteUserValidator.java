package cn.huava.sys.validation.user;

import static cn.huava.common.constant.CommonConstant.ADMIN_USER_ID;
import static cn.huava.common.constant.CommonConstant.RoleMessage.IMPORTANT_USER;

import cn.huava.sys.pojo.po.UserPo;
import cn.huava.sys.validation.common.BaseValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 删除用户前的校验器，与 {@link BeforeDeleteUser} 注解配合使用
 *
 * @author Camio1945
 */
public class BeforeDeleteUserValidator extends BaseValidator
    implements ConstraintValidator<BeforeDeleteUser, UserPo> {

  @Override
  public boolean isValid(UserPo userPo, ConstraintValidatorContext context) {
    Long id = userPo.getId();
    if (id == null) {
      return customMessage(context, "用户ID不能为空");
    }
    if (id == ADMIN_USER_ID) {
      return customMessage(context, IMPORTANT_USER);
    }
    return true;
  }
}
