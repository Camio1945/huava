package cn.huava.common.controller;

import cn.hutool.v7.swing.captcha.CaptchaUtil;
import cn.hutool.v7.swing.captcha.LineCaptcha;
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
    // width, height, codeCount, lineCount
    LineCaptcha captcha = CaptchaUtil.ofLineCaptcha(160, 60, 5, 80);

    // Save code to session
    request.getSession().setAttribute("captcha", captcha.getCode());

    // Output image
    response.setContentType("image/png");
    ImageIO.write(captcha.getImage(), "png", response.getOutputStream());
    System.out.println("Captcha: " + captcha.getCode());
  }
}
