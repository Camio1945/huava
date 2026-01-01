package cn.huava.sys.validation.user;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

///
/// # Before update user constraint
///
/// @author Camio1945
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BeforeUpdateUserValidator.class)
public @interface BeforeUpdateUser {
  String message() default "Before update user validation failed";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
