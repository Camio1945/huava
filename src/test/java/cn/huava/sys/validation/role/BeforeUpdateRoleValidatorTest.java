package cn.huava.sys.validation.role;

import static cn.huava.common.constant.CommonConstant.ADMIN_ROLE_ID;
import static cn.huava.common.constant.CommonConstant.RoleMessage.IMPORTANT_ROLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import cn.huava.sys.pojo.po.RolePo;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test for {@link BeforeUpdateRoleValidator}
 *
 * @author Camio1945
 */
@ExtendWith(MockitoExtension.class)
class BeforeUpdateRoleValidatorTest {

  @Mock private ConstraintValidatorContext context;

  private BeforeUpdateRoleValidator validator;

  @BeforeEach
  void setUp() {
    validator = new BeforeUpdateRoleValidator();
  }

  @Test
  void should_return_true_when_role_id_is_null() {
    RolePo rolePo = new RolePo();
    rolePo.setId(null);

    boolean result = validator.isValid(rolePo, context);

    assertThat(result).isTrue();
  }

  @Test
  void should_return_false_with_custom_message_when_role_id_is_admin_role_id() {
    // Mock the ConstraintViolationBuilder that is returned by buildConstraintViolationWithTemplate
    ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder =
        mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
    when(context.buildConstraintViolationWithTemplate(IMPORTANT_ROLE)).thenReturn(violationBuilder);
    when(violationBuilder.addConstraintViolation()).thenReturn(context);

    RolePo rolePo = new RolePo();
    rolePo.setId(ADMIN_ROLE_ID);

    boolean result = validator.isValid(rolePo, context);

    assertThat(result).isFalse();
    verify(context, times(1)).disableDefaultConstraintViolation();
    verify(context, times(1)).buildConstraintViolationWithTemplate(IMPORTANT_ROLE);
    verify(violationBuilder, times(1)).addConstraintViolation();
  }

  @Test
  void should_return_true_when_role_id_is_regular_role_id() {
    RolePo rolePo = new RolePo();
    rolePo.setId(2L); // Regular role ID, not admin

    boolean result = validator.isValid(rolePo, context);

    assertThat(result).isTrue();
  }
}
