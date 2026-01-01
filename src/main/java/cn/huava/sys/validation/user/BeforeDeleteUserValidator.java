package cn.huava.sys.validation.user;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

///
/// # Before delete user validator
///
/// @author Camio1945
public class BeforeDeleteUserValidator implements ConstraintValidator<BeforeDeleteUser, Object> {
  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    // Implementation will be added later when service layer is created
    return true;
  }
}
