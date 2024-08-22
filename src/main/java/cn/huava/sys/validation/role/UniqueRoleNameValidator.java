package cn.huava.sys.validation.role;

import cn.huava.common.util.Fn;
import cn.huava.sys.pojo.po.RolePo;
import cn.huava.sys.service.role.AceRoleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.dromara.hutool.core.lang.Assert;

/**
 * @author Camio1945
 */
public class UniqueRoleNameValidator implements ConstraintValidator<UniqueRoleName, RolePo> {

  @Override
  public boolean isValid(RolePo rolePo, ConstraintValidatorContext context) {
    String name = rolePo.getName();
    // when name is blank, other validator will do their job
    if (Fn.isBlank(name)) {
      return true;
    }
    boolean isUpdate = basicValidate(rolePo);
    Long id = isUpdate ? rolePo.getId() : null;
    return !Fn.getBean(AceRoleService.class).isNameExists(id, name);
  }

  private static boolean basicValidate(RolePo rolePo) {
    HttpServletRequest request = Fn.getRequest();
    boolean isCreate = request.getRequestURI().endsWith("/create");
    boolean isUpdate = request.getRequestURI().endsWith("/update");
    Assert.isTrue(isCreate || isUpdate, "目前仅允许创建或更新角色时验证名称的唯一性");
    if (isUpdate) {
      Assert.notNull(rolePo.getId(), "更新角色时，角色ID不能为空");
    }
    return isUpdate;
  }
}
