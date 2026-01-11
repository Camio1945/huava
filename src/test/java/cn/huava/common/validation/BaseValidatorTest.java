package cn.huava.common.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import cn.huava.common.pojo.po.BasePo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidatorContext;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

///
/// # Test class for BaseValidator to ensure 100% coverage
///
/// @author Camio1945
@ExtendWith(MockitoExtension.class)
class BaseValidatorTest {

  @Test
  @SneakyThrows
  void should_return_false_when_request_uri_is_not_create_or_update() {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    given(request.getRequestURI()).willReturn("/some/other/endpoint");
    BasePo basePo = new BasePo();
    try (MockedStatic<RequestContextHolder> mockedRequestContextHolder =
        Mockito.mockStatic(RequestContextHolder.class)) {
      ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
      mockedRequestContextHolder
          .when(RequestContextHolder::getRequestAttributes)
          .thenReturn(attributes);
      given(attributes.getRequest()).willReturn(request);
      // When & Then
      assertThatThrownBy(() -> BaseValidator.basicValidate(basePo))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("目前仅允许在执行创建或更新操作时验证唯一性");
    }
  }

  @Test
  @SneakyThrows
  void should_return_true_and_validate_id_for_update_operation() {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    given(request.getRequestURI()).willReturn("/some/entity/update");

    BasePo basePo = new BasePo();
    basePo.setId(1L);

    try (MockedStatic<RequestContextHolder> mockedRequestContextHolder =
        Mockito.mockStatic(RequestContextHolder.class)) {
      ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
      mockedRequestContextHolder
          .when(RequestContextHolder::getRequestAttributes)
          .thenReturn(attributes);
      given(attributes.getRequest()).willReturn(request);

      // When
      boolean result = BaseValidator.basicValidate(basePo);

      // Then
      assertThat(result).isTrue();
    }
  }

  @Test
  @SneakyThrows
  void should_throw_exception_when_updating_without_id() {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    given(request.getRequestURI()).willReturn("/some/entity/update");

    BasePo basePo = new BasePo();
    // ID is null intentionally

    try (MockedStatic<RequestContextHolder> mockedRequestContextHolder =
        Mockito.mockStatic(RequestContextHolder.class)) {
      ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
      mockedRequestContextHolder
          .when(RequestContextHolder::getRequestAttributes)
          .thenReturn(attributes);
      given(attributes.getRequest()).willReturn(request);

      // When & Then
      assertThatThrownBy(() -> BaseValidator.basicValidate(basePo))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("执行更新操作时，ID不能为空");
    }
  }

  @Test
  @SneakyThrows
  void should_return_false_for_create_operation() {
    // Given
    HttpServletRequest request = mock(HttpServletRequest.class);
    given(request.getRequestURI()).willReturn("/some/entity/create");

    BasePo basePo = new BasePo();

    try (MockedStatic<RequestContextHolder> mockedRequestContextHolder =
        Mockito.mockStatic(RequestContextHolder.class)) {
      ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
      mockedRequestContextHolder
          .when(RequestContextHolder::getRequestAttributes)
          .thenReturn(attributes);
      given(attributes.getRequest()).willReturn(request);

      // When
      boolean result = BaseValidator.basicValidate(basePo);

      // Then
      assertThat(result).isFalse();
    }
  }

  @Test
  @SneakyThrows
  void should_handle_custom_message_correctly() {
    // Given
    ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
    ConstraintValidatorContext.ConstraintViolationBuilder builder =
        mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

    given(context.buildConstraintViolationWithTemplate(any(String.class))).willReturn(builder);
    given(builder.addConstraintViolation()).willReturn(context);

    String messageTemplate = "Custom validation error";

    // When
    boolean result = BaseValidator.customMessage(context, messageTemplate);

    // Then
    assertThat(result).isFalse();
    verify(context).disableDefaultConstraintViolation();
    verify(context).buildConstraintViolationWithTemplate(messageTemplate);
    verify(builder).addConstraintViolation();
  }
}
