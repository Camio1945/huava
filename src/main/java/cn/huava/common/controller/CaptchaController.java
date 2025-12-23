package cn.huava.common.controller;

import cn.huava.common.util.SkijaCaptchaUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import java.io.IOException;

/**
 * Captcha controller
 *
 * @author Camio1945
 * @since 2025-12-20
 **/
@Controller
public class CaptchaController {

  @GetMapping("/captcha")
  public void captcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // width, height, codeCount
    SkijaCaptchaUtil.CaptchaResult captcha = SkijaCaptchaUtil.generateCaptcha(160, 60, 5);

    // Save code to session
    request.getSession().setAttribute("captcha", captcha.code());

    // Output image
    response.setContentType("image/png");
    ImageIO.write(captcha.image(), "png", response.getOutputStream());
  }
}
