package cn.huava.common.controller.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * @author Camio1945
 */
@Slf4j
class GlobalExceptionHandlerTest {
  private GlobalExceptionHandler handler;
  private ServletWebRequest webRequest;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    mockRequest.setRequestURI("/test/api");
    webRequest = new ServletWebRequest(mockRequest);
  }

  @Test
  void should_handle_IllegalArgumentException() {
    IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");
    ResponseEntity<String> response = handler.handle(exception, webRequest);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isEqualTo("Invalid argument");
  }

  @Test
  void should_handle_DuplicateKeyException() {
    DuplicateKeyException exception = new DuplicateKeyException("Duplicate entry");
    ResponseEntity<String> response = handler.handle(exception, webRequest);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isEqualTo("数据已经存在");
  }

  @Test
  void should_handle_NoResourceFoundException() {
    NoResourceFoundException exception =
        new NoResourceFoundException(HttpMethod.GET, "/nonexistent", "Resource not found");
    ResponseEntity<String> response = handler.handle(exception, webRequest);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    // The actual message returned by NoResourceFoundException includes additional text
    assertThat(response.getBody()).contains("Resource not found");
  }

  @Test
  void should_handle_MethodArgumentNotValidException() throws Exception {
    // Create a BindingResult with validation errors
    BindingResult bindingResult = mock(BindingResult.class);
    FieldError fieldError1 = new FieldError("objectName", "fieldName1", "Field 1 error");
    FieldError fieldError2 = new FieldError("objectName", "fieldName2", "Field 2 error");
    when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError1, fieldError2));
    // Create a minimal MethodParameter to avoid the NPE in getMessage()
    // We'll create a MethodParameter for a dummy method to avoid the null executable issue
    java.lang.reflect.Method dummyMethod = Object.class.getMethod("toString");
    org.springframework.core.MethodParameter methodParameter =
        new org.springframework.core.MethodParameter(dummyMethod, -1);
    MethodArgumentNotValidException exception =
        new MethodArgumentNotValidException(methodParameter, bindingResult);
    ResponseEntity<String> response = handler.handle(exception, webRequest);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).contains("Field 1 error", "Field 2 error");
    // Check that it ends with semicolon
    assertThat(response.getBody()).endsWith("；");
  }

  @Test
  void should_handle_GeneralException() {
    RuntimeException exception = new RuntimeException("Unexpected error occurred");
    ResponseEntity<String> response = handler.handleGeneralException(exception, webRequest);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isEqualTo("Unexpected error occurred");
  }

  @Test
  void should_handle_GeneralException_with_NullMessage() {
    RuntimeException exception = new RuntimeException();
    ResponseEntity<String> response = handler.handleGeneralException(exception, webRequest);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    // Body should be null when exception message is null
    assertThat(response.getBody()).isNull();
  }
}
