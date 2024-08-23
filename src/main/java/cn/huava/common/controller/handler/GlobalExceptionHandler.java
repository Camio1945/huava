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
 * @author Camio1945
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handle(IllegalArgumentException e, WebRequest request) {
    return getBadRequestRes(request, e, e.getMessage());
  }

  private static ResponseEntity<String> getBadRequestRes(
      WebRequest request, Exception e, String message) {
    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    log.warn("API {} encountered illegal argument: {}", uri, e.getMessage());
    return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DuplicateKeyException.class)
  public ResponseEntity<String> handle(DuplicateKeyException e, WebRequest request) {
    return getBadRequestRes(request, e, "数据已经存在");
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handle(MethodArgumentNotValidException e, WebRequest request) {
    String message = getMessage(e);
    return getBadRequestRes(request, e, message);
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

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<String> handle(NoResourceFoundException e, WebRequest request) {
    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    log.warn("API {} can not be handled: {}", uri, e.getMessage());
    return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGeneralException(Exception e, WebRequest request) {
    return getServerErrorRes(request, e);
  }

  private static ResponseEntity<String> getServerErrorRes(WebRequest request, Exception e) {
    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    log.error("API {} encountered server error", uri, e);
    return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
