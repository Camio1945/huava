package cn.huava.sys.controller;

import cn.huava.common.service.captcha.AceCaptchaService;
import cn.huava.sys.service.user.AceUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * Temporary test controller, all code inside will be deleted at any time.
 *
 * @author Camio1945
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/temp/test")
public class TempTestController {
  private final AceCaptchaService captchaService;
  private final AceUserService userService;

  @GetMapping("/")
  public Object test(HttpServletRequest req, HttpServletResponse resp) throws Exception {
    return null;
  }
}
