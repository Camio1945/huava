package cn.huava.common.service.captcha;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 验证码服务主入口类<br>
 * 1. If it's not GraalVM native image mode, use local dynamic captcha (use java.awt to generate
 * every time). <br>
 * 2. If it is GraalVM native image mode, try to use the online captcha API first. <br>
 * 3. If step 2 fails, use the local static captcha (the static images that have already been
 * generated). <br>
 *
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AceCaptchaService {
  private final RefreshCaptchaService refreshCaptchaService;
  private final ValidateCaptchaService validateCaptchaService;

  public void refresh(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    refreshCaptchaService.refresh(req, resp);
  }

  public void validate(
      HttpServletRequest req, String captchaCode, Boolean isCaptchaDisabledForTesting) {
    validateCaptchaService.validate(req, captchaCode, isCaptchaDisabledForTesting);
  }
}
