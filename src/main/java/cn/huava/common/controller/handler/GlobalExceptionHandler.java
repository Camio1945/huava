package cn.huava.common.controller.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理器，底层的异常直接往上抛，在这里统一做处理<br>
 *
 * @author Camio1945
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  /** IllegalArgumentException.class 异常返回 400 错误，表明是客户端的参数错误 */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handle(IllegalArgumentException e, WebRequest request) {
    return getBadRequestRes(request, e, e.getMessage());
  }

  /** MethodArgumentNotValidException.class 异常返回 400 错误，表明是客户端的参数错误 */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handle(MethodArgumentNotValidException e, WebRequest request) {
    String message = getMessage(e);
    return getBadRequestRes(request, e, message);
  }

  /** DuplicateKeyException.class 异常返回 400 错误，表明是客户端的参数错误（传了不应该传的值） */
  @ExceptionHandler(DuplicateKeyException.class)
  public ResponseEntity<String> handle(DuplicateKeyException e, WebRequest request) {
    return getBadRequestRes(request, e, "数据已经存在");
  }

  /** NoResourceFoundException.class 异常返回 400 错误，表明是客户端的参数错误（访问了不该访问的资源） */
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<String> handle(NoResourceFoundException e, WebRequest request) {
    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    log.warn("API {} can not be handled: {}", uri, e.getMessage());
    return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
  }

  /** 其他所有异常统一返回 500 服务器内部错误 */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGeneralException(Exception e, WebRequest request) {
    return getServerErrorRes(request, e);
  }

  private static ResponseEntity<String> getBadRequestRes(
      WebRequest request, Exception e, String message) {
    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    log.warn("API {} encountered illegal argument: {}", uri, e.getMessage());
    return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
  }

  private static String getMessage(MethodArgumentNotValidException e) {
    StringBuilder messages = new StringBuilder();
    e.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String errorMessage = error.getDefaultMessage();
              messages.append(errorMessage).append("；");
            });
    return messages.toString();
  }

  private static ResponseEntity<String> getServerErrorRes(WebRequest request, Exception e) {
    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    log.error("API {} encountered server error", uri, e);
    return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
