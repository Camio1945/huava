package cn.huava.common.controller;

import cn.huava.common.service.captcha.AceCaptchaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author Camio1945
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/captcha")
public class CaptchaController {
  private final AceCaptchaService aceCaptchaService;

  @GetMapping("")
  public void refresh(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    aceCaptchaService.refresh(req, resp);
  }
}
