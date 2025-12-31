package cn.huava.common.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import cn.huava.common.util.SkijaCaptchaUtil;
import jakarta.servlet.ServletOutputStream;
import java.io.IOException;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

@ExtendWith(MockitoExtension.class)
class CaptchaControllerTest {

  private final MockHttpServletRequest request = new MockHttpServletRequest();
  private final MockHttpSession session = new MockHttpSession();
  @InjectMocks private CaptchaController captchaController;
  private MockHttpServletResponse response;
  @Mock private ServletOutputStream servletOutputStream;

  @BeforeEach
  void setUp() {
    request.setSession(session);
    response =
        new MockHttpServletResponse() {
          @Override
          @NonNull
          public ServletOutputStream getOutputStream() {
            return servletOutputStream;
          }
        };
  }

  @Test
  void captchaGeneratesImageAndStoresSessionAttribute() throws IOException {
    byte[] fakeImage = new byte[10];
    String fakeCode = "TEST";
    SkijaCaptchaUtil.CaptchaResult captchaResult =
        new SkijaCaptchaUtil.CaptchaResult(fakeCode, fakeImage);

    try (MockedStatic<SkijaCaptchaUtil> mocked = mockStatic(SkijaCaptchaUtil.class)) {
      mocked
          .when(() -> SkijaCaptchaUtil.generateCaptcha(anyInt(), anyInt(), anyInt()))
          .thenReturn(captchaResult);

      captchaController.captcha(request, response);

      // Assert session state
      assertThat(session.getAttribute("captcha")).isEqualTo(fakeCode);

      // Assert response behavior
      verify(servletOutputStream).write(fakeImage);
      assertThat(response.getContentType()).isEqualTo("image/png");
    }
  }
}
