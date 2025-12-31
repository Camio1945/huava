package cn.huava.common.validation;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for EnumValidator to ensure 100% coverage
 *
 * @author Camio1945
 */
@ExtendWith(MockitoExtension.class)
class EnumValidatorTest {
  private EnumValidator validator;

  @Mock private ConstraintValidatorContext context;

  @BeforeEach
  void setUp() {
    validator = new EnumValidator();
  }

  @Test
  void testInitialize() {
    // Test that initialize method doesn't throw an exception
    ValidEnum annotation = createValidEnumAnnotation();
    assertDoesNotThrow(() -> validator.initialize(annotation));
  }

  // Helper method to create a mock annotation for testing
  private static ValidEnum createValidEnumAnnotation() {
    return new ValidEnum() {
      @Override
      public Class<? extends Enum<?>> enumClass() {
        return cn.huava.sys.enumeration.UserGenderEnum.class;
      }

      @Override
      public String message() {
        return "Invalid value";
      }

      @Override
      public Class<?>[] groups() {
        return new Class<?>[0];
      }

      @Override
      public Class<? extends Payload>[] payload() {
        return new Class[0];
      }

      @Override
      public Class<? extends java.lang.annotation.Annotation> annotationType() {
        return ValidEnum.class;
      }
    };
  }

  @Test
  void testIsValid() {
    // First initialize the validator with the annotation
    ValidEnum annotation = createValidEnumAnnotation();
    validator.initialize(annotation);

    // Test that isValid method works correctly
    assertTrue(validator.isValid(null, context)); // null should return true
    assertTrue(validator.isValid("M", context)); // valid enum value should return true
    assertTrue(validator.isValid("F", context)); // valid enum value should return true
    assertTrue(validator.isValid("U", context)); // valid enum value should return true
    assertFalse(validator.isValid("X", context)); // invalid enum value should return false
  }
}
