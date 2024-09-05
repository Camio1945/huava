package cn.huava.sys.validation.role;

import jakarta.validation.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 删除角色前的校验器注解，与 {@link BeforeDeleteRoleValidator} 配合使用
 *
 * @author Camio1945
 */
@Constraint(validatedBy = BeforeDeleteRoleValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BeforeDeleteRole {
  String message() default "";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
