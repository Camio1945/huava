package cn.huava.sys.validation.user;

import jakarta.validation.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 更新用户前的校验器注解，与 {@link BeforeUpdateUserValidator} 配合使用
 *
 * @author Camio1945
 */
@Constraint(validatedBy = BeforeUpdateUserValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BeforeUpdateUser {
  String message() default "";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
