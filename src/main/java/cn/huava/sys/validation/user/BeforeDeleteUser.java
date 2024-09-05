package cn.huava.sys.validation.user;

import jakarta.validation.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 删除用户前的校验器注解，与 {@link BeforeDeleteUserValidator} 配合使用
 *
 * @author Camio1945
 */
@Constraint(validatedBy = BeforeDeleteUserValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BeforeDeleteUser {
  String message() default "";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
