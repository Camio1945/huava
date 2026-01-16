package cn.huava.sys.validation.user;

import static cn.huava.common.constant.CommonConstant.ADMIN_USER_ID;
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
 * Test class for BeforeDeleteUserValidator to ensure 100% coverage
 *
 * @author Camio1945
 */
@ExtendWith(MockitoExtension.class)
class BeforeDeleteUserValidatorTest {

  @Mock private ConstraintValidatorContext context;

  @Mock private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

  private BeforeDeleteUserValidator validator;

  @BeforeEach
  void setUp() {
    validator = new BeforeDeleteUserValidator();
    lenient()
        .when(context.buildConstraintViolationWithTemplate(anyString()))
        .thenReturn(violationBuilder);
    lenient().when(violationBuilder.addConstraintViolation()).thenReturn(context);
  }

  @Test
  void should_return_false_and_set_custom_message_when_user_id_is_null() {
    // Given
    UserPo userPo = new UserPo();
    userPo.setId(null);

    // When
    boolean result = validator.isValid(userPo, context);

    // Then
    assertThat(result).isFalse();
    verify(context).disableDefaultConstraintViolation();
    verify(context).buildConstraintViolationWithTemplate("用户ID不能为空");
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
  void should_return_true_when_user_id_is_valid_and_not_admin() {
    // Given
    UserPo userPo = new UserPo();
    userPo.setId(2L); // Any ID except admin ID

    // When
    boolean result = validator.isValid(userPo, context);

    // Then
    assertThat(result).isTrue();
  }
}
