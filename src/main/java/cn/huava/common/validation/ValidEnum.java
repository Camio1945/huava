package cn.huava.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

///
/// # Enum type validator annotation, used together with [EnumValidator]
///
/// @author Camio1945
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidator.class)
public @interface ValidEnum {
  String message() default "Invalid value";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  Class<? extends Enum<?>> enumClass();
}
