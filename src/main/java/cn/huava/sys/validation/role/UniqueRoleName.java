package cn.huava.sys.validation.role;

import jakarta.validation.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色名称唯一性校验器注解，与 {@link UniqueRoleNameValidator} 配合使用
 *
 * @author Camio1945
 */
@Constraint(validatedBy = UniqueRoleNameValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueRoleName {
  String message() default "";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
