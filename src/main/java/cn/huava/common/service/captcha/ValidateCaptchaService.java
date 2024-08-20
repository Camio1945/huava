package cn.huava.common.service.captcha;

import cn.huava.common.constant.CommonConstant;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.lang.Assert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 1. If it not production environment, and the request needs captcha to be disabled, then ignore
 * the captcha. <br>
 * 2. Else , validate the captcha code, if not match, throw exception. <br>
 *
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
class ValidateCaptchaService {
  @Value("${spring.profiles.active}")
  private String activeEnv;

  protected void validate(
      HttpServletRequest req, String captchaCode, Boolean isCaptchaDisabledForTesting) {
    if (shouldIgnoreCaptcha(isCaptchaDisabledForTesting)) {
      return;
    }
    validateCaptchaCode(req, captchaCode);
  }

  private boolean shouldIgnoreCaptcha(Boolean isCaptchaDisabledForTesting) {
    return !activeEnv.equals(CommonConstant.ENV_PROD)
        && !activeEnv.equals(CommonConstant.ENV_PRODUCTION)
        && isCaptchaDisabledForTesting != null
        && isCaptchaDisabledForTesting;
  }

  private static void validateCaptchaCode(HttpServletRequest req, String captchaCode) {
    Assert.notBlank(captchaCode, "请输入验证码");
    String sessionCode =
        (String) req.getSession().getAttribute(CommonConstant.CAPTCHA_CODE_SESSION_KEY);
    Assert.equals(captchaCode, sessionCode, "验证码不正确，请重试");
    req.getSession().removeAttribute(CommonConstant.CAPTCHA_CODE_SESSION_KEY);
  }
}
