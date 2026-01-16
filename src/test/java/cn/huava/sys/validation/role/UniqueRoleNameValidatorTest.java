package cn.huava.sys.validation.role;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import cn.huava.common.util.Fn;
import cn.huava.sys.pojo.po.RolePo;
import cn.huava.sys.service.role.AceRoleService;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test for {@link UniqueRoleNameValidator}
 *
 * @author Camio1945
 */
@ExtendWith(MockitoExtension.class)
class UniqueRoleNameValidatorTest {

  @Mock private ConstraintValidatorContext context;

  @Mock private AceRoleService mockAceRoleService;

  private UniqueRoleNameValidator validator;

  @BeforeEach
  void setUp() {
    validator = new UniqueRoleNameValidator();
  }

  @Test
  void should_return_true_when_role_name_is_blank() {
    RolePo rolePo = new RolePo();
    rolePo.setName(""); // blank name

    try (MockedStatic<Fn> fnMock = mockStatic(Fn.class)) {
      fnMock.when(() -> Fn.isBlank("")).thenReturn(true);

      boolean result = validator.isValid(rolePo, context);

      assertThat(result).isTrue();
    }
  }

  @Test
  void should_return_true_when_role_name_is_null() {
    RolePo rolePo = new RolePo();
    rolePo.setName(null); // null name

    try (MockedStatic<Fn> fnMock = mockStatic(Fn.class)) {
      fnMock.when(() -> Fn.isBlank(null)).thenReturn(true);

      boolean result = validator.isValid(rolePo, context);

      assertThat(result).isTrue();
    }
  }

  @Test
  void should_return_true_when_role_name_is_whitespace_only() {
    RolePo rolePo = new RolePo();
    rolePo.setName("   "); // whitespace only

    try (MockedStatic<Fn> fnMock = mockStatic(Fn.class)) {
      fnMock.when(() -> Fn.isBlank("   ")).thenReturn(true);

      boolean result = validator.isValid(rolePo, context);

      assertThat(result).isTrue();
    }
  }

  @Test
  void should_return_false_when_validating_create_and_name_exists() {
    RolePo rolePo = new RolePo();
    rolePo.setName("admin");

    try (MockedStatic<Fn> fnMock = mockStatic(Fn.class)) {
      fnMock.when(() -> Fn.isBlank("admin")).thenReturn(false);

      // Create mock request inside the mocked static block
      jakarta.servlet.http.HttpServletRequest mockRequest =
          mock(jakarta.servlet.http.HttpServletRequest.class);
      when(mockRequest.getRequestURI()).thenReturn("/create");
      fnMock.when(Fn::getRequest).thenReturn(mockRequest);

      fnMock.when(() -> Fn.getBean(AceRoleService.class)).thenReturn(mockAceRoleService);

      when(mockAceRoleService.isNameExists(null, "admin")).thenReturn(true);

      boolean result = validator.isValid(rolePo, context);

      assertThat(result).isFalse();
      verify(mockAceRoleService).isNameExists(null, "admin");
    }
  }

  @Test
  void should_return_true_when_validating_create_and_name_does_not_exist() {
    RolePo rolePo = new RolePo();
    rolePo.setName("newRole");

    try (MockedStatic<Fn> fnMock = mockStatic(Fn.class)) {
      fnMock.when(() -> Fn.isBlank("newRole")).thenReturn(false);

      // Create mock request inside the mocked static block
      jakarta.servlet.http.HttpServletRequest mockRequest =
          mock(jakarta.servlet.http.HttpServletRequest.class);
      when(mockRequest.getRequestURI()).thenReturn("/create");
      fnMock.when(Fn::getRequest).thenReturn(mockRequest);

      fnMock.when(() -> Fn.getBean(AceRoleService.class)).thenReturn(mockAceRoleService);

      when(mockAceRoleService.isNameExists(null, "newRole")).thenReturn(false);

      boolean result = validator.isValid(rolePo, context);

      assertThat(result).isTrue();
      verify(mockAceRoleService).isNameExists(null, "newRole");
    }
  }

  @Test
  void should_return_false_when_validating_update_and_name_exists_for_different_id() {
    RolePo rolePo = new RolePo();
    rolePo.setId(1L);
    rolePo.setName("admin");

    try (MockedStatic<Fn> fnMock = mockStatic(Fn.class)) {
      fnMock.when(() -> Fn.isBlank("admin")).thenReturn(false);

      // Create mock request inside the mocked static block
      jakarta.servlet.http.HttpServletRequest mockRequest =
          mock(jakarta.servlet.http.HttpServletRequest.class);
      when(mockRequest.getRequestURI()).thenReturn("/update");
      fnMock.when(Fn::getRequest).thenReturn(mockRequest);

      fnMock.when(() -> Fn.getBean(AceRoleService.class)).thenReturn(mockAceRoleService);

      when(mockAceRoleService.isNameExists(1L, "admin")).thenReturn(true);

      boolean result = validator.isValid(rolePo, context);

      assertThat(result).isFalse();
      verify(mockAceRoleService).isNameExists(1L, "admin");
    }
  }

  @Test
  void should_return_true_when_validating_update_and_name_exists_for_same_id() {
    RolePo rolePo = new RolePo();
    rolePo.setId(1L);
    rolePo.setName("admin");

    try (MockedStatic<Fn> fnMock = mockStatic(Fn.class)) {
      fnMock.when(() -> Fn.isBlank("admin")).thenReturn(false);

      // Create mock request inside the mocked static block
      jakarta.servlet.http.HttpServletRequest mockRequest =
          mock(jakarta.servlet.http.HttpServletRequest.class);
      when(mockRequest.getRequestURI()).thenReturn("/update");
      fnMock.when(Fn::getRequest).thenReturn(mockRequest);

      fnMock.when(() -> Fn.getBean(AceRoleService.class)).thenReturn(mockAceRoleService);

      when(mockAceRoleService.isNameExists(1L, "admin")).thenReturn(false);

      boolean result = validator.isValid(rolePo, context);

      assertThat(result).isTrue();
      verify(mockAceRoleService).isNameExists(1L, "admin");
    }
  }
}
