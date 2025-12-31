package cn.huava.sys.validation.user;

import cn.huava.common.validation.BaseValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Unique username validator
 *
 * @author Camio1945
 */
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, Object> {
  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    // Implementation will be added later when repository is created
    return true;
  }
}