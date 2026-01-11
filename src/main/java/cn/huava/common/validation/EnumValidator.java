package cn.huava.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

///
/// # Enum type validator, used together with [ValidEnum] annotation
///
/// @author Camio1945
public class EnumValidator implements ConstraintValidator<ValidEnum, String> {
  private ValidEnum annotation;

  @Override
  public void initialize(ValidEnum annotation) {
    this.annotation = annotation;
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    }
    Object[] enumValues = annotation.enumClass().getEnumConstants();
    for (Object enumValue : enumValues) {
      if (value.equals(enumValue.toString())) {
        return true;
      }
    }
    return false;
  }
}
