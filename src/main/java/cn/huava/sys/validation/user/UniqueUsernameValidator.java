package cn.huava.sys.validation.user;

import cn.huava.common.util.Fn;
import cn.huava.sys.pojo.po.UserPo;
import cn.huava.sys.service.user.AceUserService;
import cn.huava.sys.validation.common.BaseValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author Camio1945
 */
public class UniqueUsernameValidator extends BaseValidator
    implements ConstraintValidator<UniqueUsername, UserPo> {

  @Override
  public boolean isValid(UserPo userPo, ConstraintValidatorContext context) {
    String username = userPo.getUsername();
    // when username is blank, other validator will do their job
    if (Fn.isBlank(username)) {
      return true;
    }
    boolean isUpdate = basicValidate(userPo);
    Long id = isUpdate ? userPo.getId() : null;
    return !Fn.getBean(AceUserService.class).isUsernameExists(id, username);
  }
}
