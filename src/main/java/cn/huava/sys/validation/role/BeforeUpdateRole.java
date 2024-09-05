package cn.huava.sys.validation.role;

import jakarta.validation.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 更新角色前的校验器注解，与 {@link BeforeUpdateRoleValidator} 配合使用
 *
 * @author Camio1945
 */
@Constraint(validatedBy = BeforeUpdateRoleValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BeforeUpdateRole {
  String message() default "";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
