package cn.huava.sys.validation.role;

import static cn.huava.common.constant.CommonConstant.ADMIN_ROLE_ID;
import static org.junit.jupiter.api.Assertions.assertThrows;

import cn.huava.sys.pojo.po.RolePo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BeforeDeleteRoleValidatorTest {

  private BeforeDeleteRoleValidator validator;
  private RolePo rolePo = new RolePo();

  @BeforeEach
  void setUp() {
    validator = new BeforeDeleteRoleValidator();
  }

  @Test
  void isValid() {
    assertThrows(NullPointerException.class, () -> validator.isValid(rolePo, null));
    rolePo.setId(ADMIN_ROLE_ID);
    assertThrows(NullPointerException.class, () -> validator.isValid(rolePo, null));
  }
}
