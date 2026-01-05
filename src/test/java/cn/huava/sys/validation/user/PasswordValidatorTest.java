package cn.huava.sys.validation.user;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.huava.sys.pojo.qo.UpdatePasswordQo;
import org.junit.jupiter.api.Test;

class PasswordValidatorTest {
  @Test
  void isValid() {
    PasswordValidator validator = new PasswordValidator();
    UpdatePasswordQo updatePasswordQo = new UpdatePasswordQo();
    assertTrue(validator.isValid(updatePasswordQo, null));
    updatePasswordQo.setOldPassword("hello");
    updatePasswordQo.setNewPassword(updatePasswordQo.getOldPassword());
    assertThrows(NullPointerException.class, () -> validator.isValid(updatePasswordQo, null));
  }
}
