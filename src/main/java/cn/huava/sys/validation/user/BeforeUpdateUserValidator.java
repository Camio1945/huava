package cn.huava.sys.validation.user;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

///
/// # Before update user validator
///
/// @author Camio1945
public class BeforeUpdateUserValidator implements ConstraintValidator<BeforeUpdateUser, Object> {
  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    // Implementation will be added later when service layer is created
    return true;
  }
}
