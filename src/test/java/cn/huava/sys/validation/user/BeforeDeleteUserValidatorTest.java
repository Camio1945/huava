package cn.huava.sys.validation.user;

import static cn.huava.common.constant.CommonConstant.ADMIN_USER_ID;
import static org.junit.jupiter.api.Assertions.assertThrows;

import cn.huava.sys.pojo.po.UserPo;
import org.junit.jupiter.api.Test;

class BeforeDeleteUserValidatorTest {
  @Test
  void isValid() {
    BeforeDeleteUserValidator validator = new BeforeDeleteUserValidator();
    UserPo userPo = new UserPo();
    assertThrows(NullPointerException.class, () -> validator.isValid(userPo, null));
    userPo.setId(ADMIN_USER_ID);
    assertThrows(NullPointerException.class, () -> validator.isValid(userPo, null));
  }
}
