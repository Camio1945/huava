package cn.huava.sys.validation.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cn.huava.common.util.Fn;
import cn.huava.sys.pojo.po.UserPo;
import cn.huava.sys.pojo.qo.UpdatePasswordQo;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Test class for PasswordValidator to ensure 100% coverage
 *
 * @author Camio1945
 */
@ExtendWith(MockitoExtension.class)
class PasswordValidatorTest {

  @Mock private ConstraintValidatorContext context;

  @Mock private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

  private PasswordValidator validator;

  @BeforeEach
  void setUp() {
    validator = new PasswordValidator();
    lenient()
        .when(context.buildConstraintViolationWithTemplate(anyString()))
        .thenReturn(violationBuilder);
    lenient().when(violationBuilder.addConstraintViolation()).thenReturn(context);
  }

  @Test
  void should_return_true_when_old_password_is_blank() {
    // Given
    UpdatePasswordQo updatePasswordQo = new UpdatePasswordQo();
    updatePasswordQo.setOldPassword("");
    updatePasswordQo.setNewPassword("newPassword123");

    // When
    boolean result = validator.isValid(updatePasswordQo, context);

    // Then
    assertThat(result).isTrue();
  }

  @Test
  void should_return_true_when_new_password_is_blank() {
    // Given
    UpdatePasswordQo updatePasswordQo = new UpdatePasswordQo();
    updatePasswordQo.setOldPassword("oldPassword123");
    updatePasswordQo.setNewPassword("");

    // When
    boolean result = validator.isValid(updatePasswordQo, context);

    // Then
    assertThat(result).isTrue();
  }

  @Test
  void should_return_false_and_set_custom_message_when_old_and_new_passwords_are_same() {
    // Given
    UpdatePasswordQo updatePasswordQo = new UpdatePasswordQo();
    updatePasswordQo.setOldPassword("samePassword");
    updatePasswordQo.setNewPassword("samePassword");

    // When
    boolean result = validator.isValid(updatePasswordQo, context);

    // Then
    assertThat(result).isFalse();
    verify(context).disableDefaultConstraintViolation();
    verify(context).buildConstraintViolationWithTemplate("新密码不能与旧密码相同");
  }

  @Test
  void should_return_false_and_set_custom_message_when_old_password_does_not_match() {
    // Given
    UpdatePasswordQo updatePasswordQo = new UpdatePasswordQo();
    updatePasswordQo.setOldPassword("wrongOldPassword");
    updatePasswordQo.setNewPassword("newPassword123");

    UserPo loginUser = new UserPo();
    loginUser.setPassword("encodedCorrectPassword");

    try (MockedStatic<Fn> fnMockedStatic = Mockito.mockStatic(Fn.class)) {
      fnMockedStatic.when(Fn::getLoginUser).thenReturn(loginUser);

      PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
      fnMockedStatic.when(() -> Fn.getBean(PasswordEncoder.class)).thenReturn(passwordEncoder);

      when(passwordEncoder.matches(eq("wrongOldPassword"), eq("encodedCorrectPassword")))
          .thenReturn(false);

      // When
      boolean result = validator.isValid(updatePasswordQo, context);

      // Then
      assertThat(result).isFalse();
      verify(context).disableDefaultConstraintViolation();
      verify(context).buildConstraintViolationWithTemplate("旧密码不正确");
    }
  }

  @Test
  void should_return_true_when_passwords_are_different_and_old_password_matches() {
    // Given
    UpdatePasswordQo updatePasswordQo = new UpdatePasswordQo();
    updatePasswordQo.setOldPassword("correctOldPassword");
    updatePasswordQo.setNewPassword("differentNewPassword");

    UserPo loginUser = new UserPo();
    loginUser.setPassword("encodedCorrectPassword");

    try (MockedStatic<Fn> fnMockedStatic = Mockito.mockStatic(Fn.class)) {
      fnMockedStatic.when(Fn::getLoginUser).thenReturn(loginUser);

      PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
      fnMockedStatic.when(() -> Fn.getBean(PasswordEncoder.class)).thenReturn(passwordEncoder);

      when(passwordEncoder.matches(eq("correctOldPassword"), eq("encodedCorrectPassword")))
          .thenReturn(true);

      // When
      boolean result = validator.isValid(updatePasswordQo, context);

      // Then
      assertThat(result).isTrue();
    }
  }
}
