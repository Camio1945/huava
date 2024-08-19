package cn.huava.common.service.captcha;

import cn.huava.common.constant.CommonConstant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.lang.Assert;
import org.springframework.stereotype.Service;

/**
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

  public void refresh(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    refreshCaptchaService.refresh(req, resp);
  }

  public void validate(HttpServletRequest req, String captchaCode) {
    Assert.notBlank(captchaCode, "请输入验证码");
    String sessionCode =
        (String) req.getSession().getAttribute(CommonConstant.CAPTCHA_CODE_SESSION_KEY);
    Assert.equals(captchaCode, sessionCode, "验证码不正确，请重试");
  }
}
