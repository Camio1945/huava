package cn.huava.common.util;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Tests for HttpServletUtil class
 *
 * @author Qwen
 */
@ExtendWith(MockitoExtension.class)
class HttpServletUtilTest {

  @Test
  void should_throw_IllegalArgumentException_when_request_attributes_are_null() {
    try (MockedStatic<RequestContextHolder> mockedRequestContextHolder =
        mockStatic(RequestContextHolder.class)) {

      mockedRequestContextHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(null);

      // Access the private method using reflection
      var getRequestMethod = ReflectionUtil.getMethod(HttpServletUtil.class, "getRequest");

      assertThatThrownBy(() -> invoke(getRequestMethod))
          .isInstanceOf(RuntimeException.class)
          .hasCauseInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Can not get request from thread: ");
    }
  }

  private static void invoke(Method getRequestMethod) {
    try {
      getRequestMethod.invoke(null);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e.getCause()); // Wrap the cause in a runtime exception
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e); // Also handle IllegalAccessException
    }
  }

  @Test
  void should_return_request_when_request_attributes_are_available() {
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    ServletRequestAttributes mockAttributes = mock(ServletRequestAttributes.class);
    when(mockAttributes.getRequest()).thenReturn(mockRequest);

    try (MockedStatic<RequestContextHolder> mockedRequestContextHolder =
        mockStatic(RequestContextHolder.class)) {

      mockedRequestContextHolder
          .when(RequestContextHolder::getRequestAttributes)
          .thenReturn(mockAttributes);

      // Access the private method using reflection
      var getRequestMethod = ReflectionUtil.getMethod(HttpServletUtil.class, "getRequest");
      Object result;
      try {
        result = getRequestMethod.invoke(null);
      } catch (InvocationTargetException e) {
        throw new RuntimeException(e.getCause()); // Handle the exception appropriately
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }

      assertThat(result).isEqualTo(mockRequest);
    }
  }

  // Helper class to access the protected method in HttpServletUtil
  private static class ReflectionUtil {
    public static java.lang.reflect.Method getMethod(Class<?> clazz, String methodName) {
      try {
        var method = clazz.getDeclaredMethod(methodName);
        method.setAccessible(true);
        return method;
      } catch (NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
