package cn.huava.common.validation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EnumValidatorTest {

  @Test
  void isValid() {
    EnumValidator validator = new EnumValidator();
    assertTrue(validator.isValid(null, null));
  }
}
