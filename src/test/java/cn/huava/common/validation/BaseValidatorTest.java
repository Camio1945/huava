package cn.huava.common.validation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for BaseValidator to ensure 100% coverage
 *
 * @author Camio1945
 */
@ExtendWith(MockitoExtension.class)
class BaseValidatorTest {
  private BaseValidator validator;

  @BeforeEach
  void setUp() {
    validator = new BaseValidator();
  }

  @Test
  void testConstructor() {
    // Test that constructor doesn't throw an exception
    assertNotNull(validator);
  }
}
