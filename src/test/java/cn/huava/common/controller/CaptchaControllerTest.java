package cn.huava.common.controller;

import cn.huava.common.util.SkijaCaptchaUtil;
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

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyInt;
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
    byte[] fakeImage = new byte[10];
    String fakeCode = "TEST";
    SkijaCaptchaUtil.CaptchaResult captchaResult = new SkijaCaptchaUtil.CaptchaResult(fakeCode, fakeImage);

    try (MockedStatic<SkijaCaptchaUtil> mockedSkija = mockStatic(SkijaCaptchaUtil.class)) {
      mockedSkija.when(() -> SkijaCaptchaUtil.generateCaptcha(anyInt(), anyInt(), anyInt()))
        .thenReturn(captchaResult);

      captchaController.captcha(request, response);

      verify(session).setAttribute("captcha", fakeCode);
      verify(response).setContentType("image/png");
      verify(response.getOutputStream()).write(fakeImage);
    }
  }
}
