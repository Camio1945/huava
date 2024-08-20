package cn.huava.common.controller.handler;

import cn.huava.common.pojo.dto.ApiResponseDataDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponseDataDto<Object>> handle(
      IllegalArgumentException ex, WebRequest request) {
    ApiResponseDataDto<Object> res =
        new ApiResponseDataDto<>()
            .setCode(HttpStatus.BAD_REQUEST.value())
            .setMessage(ex.getMessage());
    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    log.warn("API {} encountered illegal argument: {}", uri, ex.getMessage());
    return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponseDataDto<Object>> handleGeneralException(
      Exception ex, WebRequest request) {
    ApiResponseDataDto<Object> res =
        new ApiResponseDataDto<>()
            .setCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .setMessage(ex.getMessage());
    String uri = ((ServletWebRequest) request).getRequest().getRequestURI();
    log.error("API {} encountered server error", uri, ex);
    return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
