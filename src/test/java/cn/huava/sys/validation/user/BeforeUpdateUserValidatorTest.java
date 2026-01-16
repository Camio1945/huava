package cn.huava.sys.validation.user;

import static cn.huava.common.constant.CommonConstant.ADMIN_USER_ID;
import static cn.huava.common.constant.CommonConstant.MAX_PASSWORD_LENGTH;
import static cn.huava.common.constant.CommonConstant.MIN_PASSWORD_LENGTH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import cn.huava.sys.pojo.po.UserPo;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for BeforeUpdateUserValidator to ensure 100% coverage
 *
 * @author Camio1945
 */
@ExtendWith(MockitoExtension.class)
class BeforeUpdateUserValidatorTest {

  @Mock private ConstraintValidatorContext context;

  @Mock private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

  private BeforeUpdateUserValidator validator;

  @BeforeEach
  void setUp() {
    validator = new BeforeUpdateUserValidator();
    lenient()
        .when(context.buildConstraintViolationWithTemplate(anyString()))
        .thenReturn(violationBuilder);
    lenient().when(violationBuilder.addConstraintViolation()).thenReturn(context);
  }

  @Test
  void should_return_true_when_user_id_is_null() {
    // Given
    UserPo userPo = new UserPo();
    userPo.setId(null);

    // When
    boolean result = validator.isValid(userPo, context);

    // Then
    assertThat(result).isTrue();
  }

  @Test
  void should_return_false_and_set_custom_message_when_user_id_is_admin() {
    // Given
    UserPo userPo = new UserPo();
    userPo.setId(ADMIN_USER_ID);

    // When
    boolean result = validator.isValid(userPo, context);

    // Then
    assertThat(result).isFalse();
    verify(context).disableDefaultConstraintViolation();
    verify(context).buildConstraintViolationWithTemplate(anyString());
  }

  @Test
  void should_return_true_when_password_is_blank() {
    // Given
    UserPo userPo = new UserPo();
    userPo.setId(2L);
    userPo.setPassword(""); // Blank password

    // When
    boolean result = validator.isValid(userPo, context);

    // Then
    assertThat(result).isTrue();
  }

  @Test
  void should_return_false_and_set_custom_message_when_password_too_short() {
    // Given
    UserPo userPo = new UserPo();
    userPo.setId(2L);
    userPo.setPassword("short"); // Shorter than MIN_PASSWORD_LENGTH

    // When
    boolean result = validator.isValid(userPo, context);

    // Then
    assertThat(result).isFalse();
    verify(context).disableDefaultConstraintViolation();
    verify(context)
        .buildConstraintViolationWithTemplate(
            String.format("密码长度应该为 %d ~ %d 个字符", MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH));
  }

  @Test
  void should_return_false_and_set_custom_message_when_password_too_long() {
    // Given
    UserPo userPo = new UserPo();
    userPo.setId(2L);
    StringBuilder longPassword = new StringBuilder();
    for (int i = 0; i < MAX_PASSWORD_LENGTH + 1; i++) {
      longPassword.append("a");
    }
    userPo.setPassword(longPassword.toString()); // Longer than MAX_PASSWORD_LENGTH

    // When
    boolean result = validator.isValid(userPo, context);

    // Then
    assertThat(result).isFalse();
    verify(context).disableDefaultConstraintViolation();
    verify(context)
        .buildConstraintViolationWithTemplate(
            String.format("密码长度应该为 %d ~ %d 个字符", MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH));
  }

  @Test
  void should_return_true_when_password_length_is_valid() {
    // Given
    UserPo userPo = new UserPo();
    userPo.setId(2L);
    userPo.setPassword("validPassword123"); // Within valid length range

    // When
    boolean result = validator.isValid(userPo, context);

    // Then
    assertThat(result).isTrue();
  }
}
