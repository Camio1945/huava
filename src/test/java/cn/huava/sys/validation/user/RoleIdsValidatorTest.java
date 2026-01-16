package cn.huava.sys.validation.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cn.huava.common.util.Fn;
import cn.huava.sys.service.role.AceRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for RoleIdsValidator to ensure 100% coverage
 *
 * @author Camio1945
 */
@ExtendWith(MockitoExtension.class)
class RoleIdsValidatorTest {

  @Mock private ConstraintValidatorContext context;

  @Mock private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

  private RoleIdsValidator validator;

  @BeforeEach
  void setUp() {
    validator = new RoleIdsValidator();
    lenient()
        .when(context.buildConstraintViolationWithTemplate(anyString()))
        .thenReturn(violationBuilder);
    lenient().when(violationBuilder.addConstraintViolation()).thenReturn(context);
  }

  @Test
  void should_return_false_and_set_custom_message_when_role_ids_is_empty() {
    // Given
    List<Long> roleIds = Collections.emptyList();

    // When
    boolean result = validator.isValid(roleIds, context);

    // Then
    assertThat(result).isFalse();
    verify(context).disableDefaultConstraintViolation();
    verify(context).buildConstraintViolationWithTemplate("角色不能为空");
  }

  @Test
  void should_return_false_and_set_custom_message_when_role_ids_is_null() {
    // Given
    List<Long> roleIds = null;

    // When
    boolean result = validator.isValid(roleIds, context);

    // Then
    assertThat(result).isFalse();
    verify(context).disableDefaultConstraintViolation();
    verify(context).buildConstraintViolationWithTemplate("角色不能为空");
  }

  @Test
  void should_return_false_and_set_custom_message_when_some_roles_do_not_exist_in_db() {
    // Given
    List<Long> roleIds = Arrays.asList(1L, 2L, 3L);
    AceRoleService service = mock(AceRoleService.class);

    try (MockedStatic<Fn> fnMockedStatic = Mockito.mockStatic(Fn.class)) {
      fnMockedStatic.when(() -> Fn.getBean(AceRoleService.class)).thenReturn(service);

      // Simulate that only 2 out of 3 roles exist in DB
      when(service.count(any(LambdaQueryWrapper.class))).thenReturn(2L);

      // When
      boolean result = validator.isValid(roleIds, context);

      // Then
      assertThat(result).isFalse();
      verify(context).disableDefaultConstraintViolation();
      verify(context).buildConstraintViolationWithTemplate("角色不存在");
    }
  }

  @Test
  void should_return_true_when_all_roles_exist_in_db() {
    // Given
    List<Long> roleIds = Arrays.asList(1L, 2L, 3L);
    AceRoleService service = mock(AceRoleService.class);

    try (MockedStatic<Fn> fnMockedStatic = Mockito.mockStatic(Fn.class)) {
      fnMockedStatic.when(() -> Fn.getBean(AceRoleService.class)).thenReturn(service);

      // Simulate that all 3 roles exist in DB
      when(service.count(any(LambdaQueryWrapper.class))).thenReturn(3L);

      // When
      boolean result = validator.isValid(roleIds, context);

      // Then
      assertThat(result).isTrue();
    }
  }
}
