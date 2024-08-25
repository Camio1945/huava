package cn.huava.common.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Camio1945
 */
class HttpServletUtil {
  private HttpServletUtil() {}

  /** Get the current request. (has no use in new Thread) */
  protected static @NonNull HttpServletRequest getRequest() {
    ServletRequestAttributes requestAttributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (requestAttributes == null) {
      throw new IllegalArgumentException(
          "Can not get request from thread: " + Thread.currentThread());
    }
    return requestAttributes.getRequest();
  }
}
