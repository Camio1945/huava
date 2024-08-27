package cn.huava.sys.validation.user;

import jakarta.validation.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Camio1945
 */
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
  String message() default "";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
