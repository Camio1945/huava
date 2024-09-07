package cn.huava.sys.validation.role;

import static cn.huava.common.constant.CommonConstant.ADMIN_ROLE_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import cn.huava.sys.pojo.po.RolePo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BeforeUpdateRoleValidatorTest {

  private BeforeUpdateRoleValidator validator;
  private RolePo rolePo = new RolePo();

  @BeforeEach
  void setUp() {
    validator = new BeforeUpdateRoleValidator();
  }

  @Test
  void idIsNull() {
    rolePo.setId(null);
    assertTrue(validator.isValid(rolePo, null));
  }

  @Test
  void idIsAdminRole() {
    rolePo.setId(ADMIN_ROLE_ID);
    assertThrows(NullPointerException.class, () -> validator.isValid(rolePo, null));
  }

  @Test
  void idIsNotAdminRole() {
    rolePo.setId(2L);
    assertTrue(validator.isValid(rolePo, null));
  }
}
