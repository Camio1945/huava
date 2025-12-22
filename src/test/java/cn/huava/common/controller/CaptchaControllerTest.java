package cn.huava.common.controller;

import cn.hutool.v7.swing.captcha.CaptchaUtil;
import cn.hutool.v7.swing.captcha.LineCaptcha;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaptchaControllerTest {

  @InjectMocks
  private CaptchaController captchaController;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private HttpSession session;

  @Mock
  private ServletOutputStream servletOutputStream;

  @BeforeEach
  void setUp() throws IOException {
    when(response.getOutputStream()).thenReturn(servletOutputStream);
    when(request.getSession()).thenReturn(session);
  }

  @Test
  void captchaGeneratesImageAndSetsSessionAttribute() throws IOException {
    try (MockedStatic<CaptchaUtil> mockedCaptchaUtil = mockStatic(CaptchaUtil.class);
         MockedStatic<ImageIO> mockedImageIO = mockStatic(ImageIO.class)) {

      LineCaptcha mockLineCaptcha = mock(LineCaptcha.class);
      when(mockLineCaptcha.getCode()).thenReturn("ABCDE");
      when(mockLineCaptcha.getImage()).thenReturn(mock(java.awt.image.BufferedImage.class)); // Return a mocked BufferedImage

      mockedCaptchaUtil
        .when(() -> CaptchaUtil.ofLineCaptcha(anyInt(), anyInt(), anyInt(), anyInt()))
        .thenReturn(mockLineCaptcha);

      captchaController.captcha(request, response);

      verify(response).setContentType("image/png");
      verify(session).setAttribute(eq("captcha"), eq("ABCDE"));
      mockedImageIO.verify(
        () -> ImageIO.write(any(RenderedImage.class), eq("png"), eq(servletOutputStream)));
      mockedCaptchaUtil.verify(() -> CaptchaUtil.ofLineCaptcha(160, 60, 5, 80));
    }
  }
}
