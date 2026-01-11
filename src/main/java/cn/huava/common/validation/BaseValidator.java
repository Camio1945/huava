package cn.huava.common.validation;

import cn.huava.common.pojo.po.BasePo;
import cn.huava.common.util.Fn;
import cn.hutool.v7.core.lang.Assert;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidatorContext;

///
/// # Base validator class
///
/// About @SuppressWarnings("java:S1118"):
///   This rule says: Utility classes should not have public constructors.
///   But our BaseValidator class need to be inherited, can not provide a private constructor.
///
/// @author Camio1945
///
///
@SuppressWarnings("java:S1118")
public class BaseValidator {

  public static boolean basicValidate(BasePo basePo) {
    HttpServletRequest request = Fn.getRequest();
    boolean isCreate = request.getRequestURI().endsWith("/create");
    boolean isUpdate = request.getRequestURI().endsWith("/update");
    Assert.isTrue(isCreate || isUpdate, "目前仅允许在执行创建或更新操作时验证唯一性");
    if (isUpdate) {
      Assert.notNull(basePo.getId(), "执行更新操作时，ID不能为空");
    }
    return isUpdate;
  }

  public static boolean customMessage(ConstraintValidatorContext context, String messageTemplate) {
    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(messageTemplate).addConstraintViolation();
    return false;
  }
}
