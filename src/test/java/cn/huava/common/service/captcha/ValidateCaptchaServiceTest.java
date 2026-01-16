package cn.huava.common.service.captcha;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import cn.huava.common.constant.CommonConstant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Test class for ValidateCaptchaService
 *
 * @author Camio1945
 */
@ExtendWith(MockitoExtension.class)
class ValidateCaptchaServiceTest {

  @Mock private HttpServletRequest mockRequest;
  @Mock private HttpSession mockSession;

  private ValidateCaptchaService validateCaptchaService;

  @BeforeEach
  void setUp() {
    validateCaptchaService = new ValidateCaptchaService();
    ReflectionTestUtils.setField(validateCaptchaService, "activeEnv", "dev");
  }

  @Test
  void should_ignore_captcha_validation_when_not_production_and_disabled_for_testing() {
    // Given
    String captchaCode = "ABC123";
    Boolean isCaptchaDisabledForTesting = true;

    // When & Then - should not throw any exception
    assertThatCode(
            () ->
                validateCaptchaService.validate(
                    mockRequest, captchaCode, isCaptchaDisabledForTesting))
        .doesNotThrowAnyException();
  }

  @Test
  void should_ignore_captcha_validation_when_not_production_and_disabled_for_testing_is_true() {
    // Given
    String captchaCode = "ABC123";
    Boolean isCaptchaDisabledForTesting = true;

    // Set active environment to non-production
    ReflectionTestUtils.setField(validateCaptchaService, "activeEnv", "test");

    // When & Then - should not throw any exception
    assertThatCode(
            () ->
                validateCaptchaService.validate(
                    mockRequest, captchaCode, isCaptchaDisabledForTesting))
        .doesNotThrowAnyException();
  }

  @Test
  void should_not_ignore_captcha_validation_when_in_production_environment() {
    // Given
    String captchaCode = "ABC123";
    Boolean isCaptchaDisabledForTesting = true;

    // Set active environment to production
    ReflectionTestUtils.setField(validateCaptchaService, "activeEnv", CommonConstant.ENV_PROD);

    // Mock session behavior
    when(mockRequest.getSession()).thenReturn(mockSession);
    when(mockSession.getAttribute(CommonConstant.CAPTCHA_CODE_SESSION_KEY)).thenReturn(captchaCode);

    // When & Then - should proceed with validation
    assertThatCode(
            () ->
                validateCaptchaService.validate(
                    mockRequest, captchaCode, isCaptchaDisabledForTesting))
        .doesNotThrowAnyException();

    // Verify session attribute was removed
    verify(mockSession).removeAttribute(CommonConstant.CAPTCHA_CODE_SESSION_KEY);
  }

  @Test
  void
      should_not_ignore_captcha_validation_when_in_production_environment_with_production_constant() {
    // Given
    String captchaCode = "ABC123";
    Boolean isCaptchaDisabledForTesting = true;

    // Set active environment to PRODUCTION constant
    ReflectionTestUtils.setField(
        validateCaptchaService, "activeEnv", CommonConstant.ENV_PRODUCTION);

    // Mock session behavior
    when(mockRequest.getSession()).thenReturn(mockSession);
    when(mockSession.getAttribute(CommonConstant.CAPTCHA_CODE_SESSION_KEY)).thenReturn(captchaCode);

    // When & Then - should proceed with validation
    assertThatCode(
            () ->
                validateCaptchaService.validate(
                    mockRequest, captchaCode, isCaptchaDisabledForTesting))
        .doesNotThrowAnyException();

    // Verify session attribute was removed
    verify(mockSession).removeAttribute(CommonConstant.CAPTCHA_CODE_SESSION_KEY);
  }

  @Test
  void should_validate_captcha_code_successfully_when_codes_match() {
    // Given
    String captchaCode = "ABC123";
    Boolean isCaptchaDisabledForTesting = null; // Not disabled for testing

    // Mock session behavior
    when(mockRequest.getSession()).thenReturn(mockSession);
    when(mockSession.getAttribute(CommonConstant.CAPTCHA_CODE_SESSION_KEY)).thenReturn(captchaCode);

    // When & Then - should not throw any exception
    assertThatCode(
            () ->
                validateCaptchaService.validate(
                    mockRequest, captchaCode, isCaptchaDisabledForTesting))
        .doesNotThrowAnyException();

    // Verify session attribute was removed
    verify(mockSession).removeAttribute(CommonConstant.CAPTCHA_CODE_SESSION_KEY);
  }

  @Test
  void should_throw_exception_when_captcha_code_is_blank() {
    // Given
    String captchaCode = "";
    Boolean isCaptchaDisabledForTesting = null;

    // When & Then
    assertThatThrownBy(
            () ->
                validateCaptchaService.validate(
                    mockRequest, captchaCode, isCaptchaDisabledForTesting))
        .hasMessage("请输入验证码");
  }

  @Test
  void should_throw_exception_when_captcha_code_is_null() {
    // Given
    String captchaCode = null;
    Boolean isCaptchaDisabledForTesting = null;

    // When & Then
    assertThatThrownBy(
            () ->
                validateCaptchaService.validate(
                    mockRequest, captchaCode, isCaptchaDisabledForTesting))
        .hasMessage("请输入验证码");
  }

  @Test
  void should_throw_exception_when_captcha_code_does_not_match_session_code() {
    // Given
    String captchaCode = "ABC123";
    String sessionCode = "XYZ789";
    Boolean isCaptchaDisabledForTesting = null;

    // Mock session behavior
    when(mockRequest.getSession()).thenReturn(mockSession);
    when(mockSession.getAttribute(CommonConstant.CAPTCHA_CODE_SESSION_KEY)).thenReturn(sessionCode);

    // When & Then
    assertThatThrownBy(
            () ->
                validateCaptchaService.validate(
                    mockRequest, captchaCode, isCaptchaDisabledForTesting))
        .hasMessage("验证码不正确，请重试");
  }

  @Test
  void should_remove_session_attribute_after_successful_validation() {
    // Given
    String captchaCode = "ABC123";
    Boolean isCaptchaDisabledForTesting = null;

    // Mock session behavior
    when(mockRequest.getSession()).thenReturn(mockSession);
    when(mockSession.getAttribute(CommonConstant.CAPTCHA_CODE_SESSION_KEY)).thenReturn(captchaCode);

    // When
    validateCaptchaService.validate(mockRequest, captchaCode, isCaptchaDisabledForTesting);

    // Then
    verify(mockSession).removeAttribute(CommonConstant.CAPTCHA_CODE_SESSION_KEY);
  }

  @Test
  void should_not_remove_session_attribute_when_validation_fails_due_to_mismatch() {
    // Given
    String captchaCode = "ABC123";
    String sessionCode = "XYZ789";
    Boolean isCaptchaDisabledForTesting = null;

    // Mock session behavior
    when(mockRequest.getSession()).thenReturn(mockSession);
    when(mockSession.getAttribute(CommonConstant.CAPTCHA_CODE_SESSION_KEY)).thenReturn(sessionCode);

    // When & Then
    assertThatThrownBy(
            () ->
                validateCaptchaService.validate(
                    mockRequest, captchaCode, isCaptchaDisabledForTesting))
        .hasMessage("验证码不正确，请重试");

    // Verify that removeAttribute was not called due to early failure
    verify(mockSession, never()).removeAttribute(CommonConstant.CAPTCHA_CODE_SESSION_KEY);
  }

  @Test
  void should_not_remove_session_attribute_when_validation_fails_due_to_blank_code() {
    // Given
    String captchaCode = "";
    Boolean isCaptchaDisabledForTesting = null;

    // When & Then
    assertThatThrownBy(
            () ->
                validateCaptchaService.validate(
                    mockRequest, captchaCode, isCaptchaDisabledForTesting))
        .hasMessage("请输入验证码");

    // Verify that removeAttribute was not called due to early failure
    verify(mockSession, never()).removeAttribute(CommonConstant.CAPTCHA_CODE_SESSION_KEY);
  }

  @Test
  void should_not_remove_session_attribute_when_validation_fails_due_to_null_code() {
    // Given
    String captchaCode = null;
    Boolean isCaptchaDisabledForTesting = null;

    // When & Then
    assertThatThrownBy(
            () ->
                validateCaptchaService.validate(
                    mockRequest, captchaCode, isCaptchaDisabledForTesting))
        .hasMessage("请输入验证码");

    // Verify that removeAttribute was not called due to early failure
    verify(mockSession, never()).removeAttribute(CommonConstant.CAPTCHA_CODE_SESSION_KEY);
  }

  @Test
  void should_not_ignore_captcha_when_isCaptchaDisabledForTesting_is_false() {
    // Given
    String captchaCode = "ABC123";
    Boolean isCaptchaDisabledForTesting = false;

    // Mock session behavior
    when(mockRequest.getSession()).thenReturn(mockSession);
    when(mockSession.getAttribute(CommonConstant.CAPTCHA_CODE_SESSION_KEY)).thenReturn(captchaCode);

    // When & Then - should proceed with validation despite not being in production
    assertThatCode(
            () ->
                validateCaptchaService.validate(
                    mockRequest, captchaCode, isCaptchaDisabledForTesting))
        .doesNotThrowAnyException();

    // Verify session attribute was removed
    verify(mockSession).removeAttribute(CommonConstant.CAPTCHA_CODE_SESSION_KEY);
  }

  @Test
  void should_not_ignore_captcha_when_isCaptchaDisabledForTesting_is_null() {
    // Given
    String captchaCode = "ABC123";
    Boolean isCaptchaDisabledForTesting = null;

    // Mock session behavior
    when(mockRequest.getSession()).thenReturn(mockSession);
    when(mockSession.getAttribute(CommonConstant.CAPTCHA_CODE_SESSION_KEY)).thenReturn(captchaCode);

    // When & Then - should proceed with validation despite not being in production
    assertThatCode(
            () ->
                validateCaptchaService.validate(
                    mockRequest, captchaCode, isCaptchaDisabledForTesting))
        .doesNotThrowAnyException();

    // Verify session attribute was removed
    verify(mockSession).removeAttribute(CommonConstant.CAPTCHA_CODE_SESSION_KEY);
  }

  @Test
  void should_ignore_captcha_when_environment_is_dev_and_disabled_for_testing() {
    // Given
    String captchaCode = "ABC123";
    Boolean isCaptchaDisabledForTesting = true;

    // Set active environment to dev (non-production)
    ReflectionTestUtils.setField(validateCaptchaService, "activeEnv", "dev");

    // When & Then - should not throw any exception and should ignore validation
    assertThatCode(
            () ->
                validateCaptchaService.validate(
                    mockRequest, captchaCode, isCaptchaDisabledForTesting))
        .doesNotThrowAnyException();

    // Verify that getSession was never called since validation was skipped
    verify(mockRequest, never()).getSession();
  }

  @Test
  void should_ignore_captcha_when_environment_is_test_and_disabled_for_testing() {
    // Given
    String captchaCode = "ABC123";
    Boolean isCaptchaDisabledForTesting = true;

    // Set active environment to test (non-production)
    ReflectionTestUtils.setField(validateCaptchaService, "activeEnv", "test");

    // When & Then - should not throw any exception and should ignore validation
    assertThatCode(
            () ->
                validateCaptchaService.validate(
                    mockRequest, captchaCode, isCaptchaDisabledForTesting))
        .doesNotThrowAnyException();

    // Verify that getSession was never called since validation was skipped
    verify(mockRequest, never()).getSession();
  }
}
