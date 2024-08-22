package cn.huava.common.controller.handler;

import cn.huava.common.pojo.dto.ResDto;
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
  private static final String DUPLICATE_ENTRY = "Duplicate entry";

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ResDto<Object>> handle(IllegalArgumentException e, WebRequest request) {
    return getBadRequestRes(request, e, e.getMessage());
  }

  private static ResponseEntity<ResDto<Object>> getBadRequestRes(
      WebRequest request, Exception e, String message) {
    ResDto<Object> res = new ResDto<>().setCode(HttpStatus.BAD_REQUEST.value()).setMessage(message);
    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    log.warn("API {} encountered illegal argument: {}", uri, e.getMessage());
    return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DuplicateKeyException.class)
  public ResponseEntity<ResDto<Object>> handle(DuplicateKeyException e, WebRequest request) {
    return getBadRequestRes(request, e, "数据已经存在");
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ResDto<Object>> handle(
      MethodArgumentNotValidException e, WebRequest request) {
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
  public ResponseEntity<ResDto<Object>> handle(NoResourceFoundException e, WebRequest request) {
    ResDto<Object> res =
        new ResDto<>().setCode(HttpStatus.NOT_FOUND.value()).setMessage(e.getMessage());
    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    log.warn("API {} can not be handled: {}", uri, e.getMessage());
    return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ResDto<Object>> handleGeneralException(Exception e, WebRequest request) {
    return getServerErrorRes(request, e);
  }

  private static ResponseEntity<ResDto<Object>> getServerErrorRes(WebRequest request, Exception e) {
    ResDto<Object> res =
        new ResDto<>().setCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).setMessage(e.getMessage());
    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    log.error("API {} encountered server error", uri, e);
    return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
