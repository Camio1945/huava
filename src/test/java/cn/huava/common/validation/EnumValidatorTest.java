package cn.huava.common.validation;

import static org.assertj.core.api.Assertions.assertThat;

import cn.huava.sys.enumeration.PermTypeEnum;
import jakarta.validation.Payload;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

/**
 * @author Camio1945
 */
class EnumValidatorTest {
  private final EnumValidator validator = new EnumValidator();

  @Test
  @SneakyThrows
  void should_return_true_for_valid_enum_value() {
    ValidEnum validEnum = createValidEnum();
    validator.initialize(validEnum);
    assertThat(validator.isValid("D", null)).isTrue();
    assertThat(validator.isValid("M", null)).isTrue();
    assertThat(validator.isValid("E", null)).isTrue();
    assertThat(validator.isValid("X", null)).isFalse();
  }

  private ValidEnum createValidEnum() {
    return new ValidEnum() {
      @Override
      public Class<? extends Enum<?>> enumClass() {
        return PermTypeEnum.class;
      }

      @Override
      public String message() {
        return "Invalid value";
      }

      @Override
      public Class<?>[] groups() {
        return new Class[0];
      }

      @SuppressWarnings("unchecked")
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
  @SneakyThrows
  void should_return_true_for_null_value() {
    ValidEnum validEnum = createValidEnum();
    validator.initialize(validEnum);
    boolean result = validator.isValid(null, null);
    assertThat(result).isTrue();
  }

  @Test
  @SneakyThrows
  void should_return_false_for_empty_string_that_is_not_in_enum() {
    ValidEnum validEnum = createValidEnum();
    validator.initialize(validEnum);
    boolean result = validator.isValid("", null);
    assertThat(result).isFalse();
  }

  @Test
  @SneakyThrows
  void should_return_false_for_whitespace_string_that_is_not_in_enum() {
    ValidEnum validEnum = createValidEnum();
    validator.initialize(validEnum);
    boolean result = validator.isValid(" ", null);
    assertThat(result).isFalse();
  }
}
