package cn.huava.sys.validation.role;

import static cn.huava.common.constant.CommonConstant.ADMIN_ROLE_ID;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.huava.sys.pojo.po.RolePo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UniqueRoleNameValidatorTest {

  private UniqueRoleNameValidator validator;
  private RolePo rolePo = new RolePo();

  @BeforeEach
  void setUp() {
    validator = new UniqueRoleNameValidator();
  }

  @Test
  void idIsAdminRole() {
    rolePo.setId(ADMIN_ROLE_ID);
    assertTrue(validator.isValid(rolePo, null));
  }
}
