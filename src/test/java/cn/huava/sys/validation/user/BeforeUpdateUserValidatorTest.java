package cn.huava.sys.validation.user;

import static cn.huava.common.constant.CommonConstant.ADMIN_ROLE_ID;
import static org.junit.jupiter.api.Assertions.*;

import cn.huava.sys.pojo.po.UserPo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BeforeUpdateUserValidatorTest {

  private BeforeUpdateUserValidator validator;
  private UserPo userPo = new UserPo();

  @BeforeEach
  void setUp() {
    validator = new BeforeUpdateUserValidator();
  }

  @Test
  void idIsNull() {
    userPo.setId(null);
    assertTrue(validator.isValid(userPo, null));
  }

  @Test
  void idIsAdminUser() {
    userPo.setId(ADMIN_ROLE_ID);
    assertThrows(NullPointerException.class, () -> validator.isValid(userPo, null));
  }

  @Test
  void idIsNotAdminUser() {
    userPo.setId(2L);
    assertTrue(validator.isValid(userPo, null));
  }
}
