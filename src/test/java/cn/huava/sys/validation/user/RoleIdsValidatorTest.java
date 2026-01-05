package cn.huava.sys.validation.user;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class RoleIdsValidatorTest {
  @Test
  void isValid() {
    List<Long> roleIds = new ArrayList<>();
    RoleIdsValidator validator = new RoleIdsValidator();
    assertThrows(NullPointerException.class, () -> validator.isValid(roleIds, null));
  }
}
