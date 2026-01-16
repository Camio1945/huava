package cn.huava.sys.validation.role;

import static cn.huava.common.constant.CommonConstant.ADMIN_ROLE_ID;
import static cn.huava.common.constant.CommonConstant.RoleMessage.IMPORTANT_ROLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import cn.huava.common.util.Fn;
import cn.huava.sys.pojo.po.RolePo;
import cn.huava.sys.service.userrole.AceUserRoleService;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test for {@link BeforeDeleteRoleValidator}
 *
 * @author Camio1945
 */
@ExtendWith(MockitoExtension.class)
class BeforeDeleteRoleValidatorTest {

  private BeforeDeleteRoleValidator validator;
  private ConstraintValidatorContext context;
  private AceUserRoleService userRoleService;

  private MockedStatic<Fn> fnMock;

  @BeforeEach
  void setUp() {
    validator = new BeforeDeleteRoleValidator();
    context = mock(ConstraintValidatorContext.class);
    userRoleService = mock(AceUserRoleService.class);

    fnMock = mockStatic(Fn.class);
    fnMock.when(() -> Fn.getBean(AceUserRoleService.class)).thenReturn(userRoleService);
  }

  @AfterEach
  void tearDown() {
    fnMock.close();
  }

  @Test
  void should_return_false_with_custom_message_when_role_id_is_null() {
    // Given
    RolePo rolePo = new RolePo();
    rolePo.setId(null);

    ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder =
        mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
    when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
    when(violationBuilder.addConstraintViolation()).thenReturn(context);

    // When
    boolean result = validator.isValid(rolePo, context);

    // Then
    assertThat(result).isFalse();
    verify(context, times(1)).disableDefaultConstraintViolation();
    verify(context, times(1)).buildConstraintViolationWithTemplate("角色ID不能为空");
    verify(violationBuilder, times(1)).addConstraintViolation();
  }

  @Test
  void should_return_false_with_custom_message_when_role_id_is_admin_role_id() {
    // Given
    RolePo rolePo = new RolePo();
    rolePo.setId(ADMIN_ROLE_ID);

    ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder =
        mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
    when(context.buildConstraintViolationWithTemplate(IMPORTANT_ROLE)).thenReturn(violationBuilder);
    when(violationBuilder.addConstraintViolation()).thenReturn(context);

    // When
    boolean result = validator.isValid(rolePo, context);

    // Then
    assertThat(result).isFalse();
    verify(context, times(1)).disableDefaultConstraintViolation();
    verify(context, times(1)).buildConstraintViolationWithTemplate(IMPORTANT_ROLE);
    verify(violationBuilder, times(1)).addConstraintViolation();
  }

  @Test
  void should_return_false_with_custom_message_when_role_has_users() {
    // Given
    RolePo rolePo = new RolePo();
    rolePo.setId(2L); // Regular role ID

    when(userRoleService.countUserByRoleId(2L)).thenReturn(5L); // 5 users assigned to this role

    ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder =
        mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
    when(context.buildConstraintViolationWithTemplate("角色下存在用户，不能删除")).thenReturn(violationBuilder);
    when(violationBuilder.addConstraintViolation()).thenReturn(context);

    // When
    boolean result = validator.isValid(rolePo, context);

    // Then
    assertThat(result).isFalse();
    verify(context, times(1)).disableDefaultConstraintViolation();
    verify(context, times(1)).buildConstraintViolationWithTemplate("角色下存在用户，不能删除");
    verify(violationBuilder, times(1)).addConstraintViolation();
    verify(userRoleService, times(1)).countUserByRoleId(2L);
  }

  @Test
  void should_return_true_when_role_has_no_users() {
    // Given
    RolePo rolePo = new RolePo();
    rolePo.setId(2L); // Regular role ID

    when(userRoleService.countUserByRoleId(2L)).thenReturn(0L); // No users assigned to this role

    // When
    boolean result = validator.isValid(rolePo, context);

    // Then
    assertThat(result).isTrue();
    verify(userRoleService, times(1)).countUserByRoleId(2L);
  }
}
