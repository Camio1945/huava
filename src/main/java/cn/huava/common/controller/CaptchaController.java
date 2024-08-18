package cn.huava.common.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.swing.captcha.CaptchaUtil;
import org.dromara.hutool.swing.captcha.ShearCaptcha;
import org.dromara.hutool.swing.captcha.generator.MathGenerator;
import org.springframework.web.bind.annotation.*;

/**
 * @author Camio1945
 */
@Slf4j
@RestController
@RequestMapping("/captcha")
public class CaptchaController {

  @GetMapping("")
  public void generate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    ShearCaptcha captcha = CaptchaUtil.ofShearCaptcha(100, 40, 4, 4);
    captcha.setGenerator(new MathGenerator(1));
    captcha.createCode();
    req.getSession().setAttribute("captchaCode", captcha.getCode());
    resp.setContentType("image/png");
    captcha.write(resp.getOutputStream());
  }
}
