package cn.huava.sys.validation.user;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Before delete user constraint
 *
 * @author Camio1945
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BeforeDeleteUserValidator.class)
public @interface BeforeDeleteUser {
  String message() default "Before delete user validation failed";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}