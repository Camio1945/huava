package cn.huava.common.service.captcha;

import cn.huava.common.constant.CommonConstant;
import cn.huava.common.util.SingleFlightUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.io.file.FileNameUtil;
import org.dromara.hutool.core.io.file.FileUtil;
import org.dromara.hutool.core.text.*;
import org.dromara.hutool.core.util.RandomUtil;
import org.dromara.hutool.http.HttpUtil;
import org.dromara.hutool.json.JSONObject;
import org.dromara.hutool.json.JSONUtil;
import org.dromara.hutool.swing.captcha.*;
import org.graalvm.nativeimage.ImageInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.*;

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
public class RefreshCaptchaService {
  private static Resource[] localStaticImages = null;

  private static String appId;

  private static String appSecret;

  @Value("${project.mxnzp_roll_api.app_id_path}")
  private String appIdPath;

  @Value("${project.mxnzp_roll_api.app_secret_path}")
  private String appSecretPath;

  protected void refresh(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    if (ImageInfo.inImageRuntimeCode()) {
      refreshForNativeImage(req, resp);
    } else {
      refreshForJava(req, resp);
    }
  }

  private void refreshForNativeImage(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    if (refreshByOnlineApi(req, resp)) {
      return;
    }
    refreshByLocalStaticImage(req, resp);
  }

  private void refreshForJava(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    LineCaptcha captcha = CaptchaUtil.ofLineCaptcha(100, 40, 4, 4);
    req.getSession().setAttribute(CommonConstant.CAPTCHA_CODE_SESSION_KEY, captcha.getCode());
    writeResponse(resp, captcha.getImageBytes());
  }

  private boolean refreshByOnlineApi(HttpServletRequest req, HttpServletResponse resp) {
    initAppIdIfNeeded(appIdPath);
    initAppSecretIfNeeded(appSecretPath);
    if (StrValidator.isBlank(appId) || StrValidator.isBlank(appSecret)) {
      return false;
    }
    try {
      JSONObject jsonObject = getJsonFromOnlineApi(appId, appSecret);
      writeBase64ImageToResponse(req, resp, jsonObject);
      return true;
    } catch (Exception e) {
      log.error("refresh captcha by online api error", e);
    }
    return false;
  }

  private void refreshByLocalStaticImage(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    Resource resource = getRandomLocalStaticImage();
    String filename = resource.getFilename();
    String captchaCode = FileNameUtil.mainName(filename);
    req.getSession().setAttribute(CommonConstant.CAPTCHA_CODE_SESSION_KEY, captchaCode);
    writeResponse(resp, resourceToBytes(resource));
  }

  private static void writeResponse(HttpServletResponse resp, byte[] imageBytes)
      throws IOException {
    resp.setContentType("image/jpeg");
    resp.getOutputStream().write(imageBytes);
    resp.getOutputStream().flush();
  }

  private static void initAppIdIfNeeded(String appIdPath) {
    if (appId != null) {
      return;
    }
    appId = SingleFlightUtil.execute("mxnzpRollApiAppId", () -> readContent(appIdPath));
    if (StrValidator.isBlank(appId)) {
      log.warn(
          "project.mxnzp_roll_api.app_id_path is blank, will use local static image instead of online captcha");
    }
  }

  private static void initAppSecretIfNeeded(String appSecretPath) {
    if (appSecret != null) {
      return;
    }
    appSecret = SingleFlightUtil.execute("mxnzpRollApiAppSecret", () -> readContent(appSecretPath));
    if (StrValidator.isBlank(appId)) {
      log.warn(
          "project.mxnzp_roll_api.app_secret_path is blank, will use local static image instead of online captcha");
    }
  }

  private static JSONObject getJsonFromOnlineApi(String appId, String appSecret) {
    // type: 0-image link, 1-base64 image
    String urlTemplate =
        "https://www.mxnzp.com/api/verifycode/code?len=4&type=1&app_id={}&app_secret={}";
    String url = CharSequenceUtil.format(urlTemplate, appId, appSecret);
    String jsonStr = HttpUtil.get(url);
    return JSONUtil.parseObj(jsonStr);
  }

  private static void writeBase64ImageToResponse(
      HttpServletRequest req, HttpServletResponse resp, JSONObject jsonObject) throws IOException {
    JSONObject dataJson = jsonObject.getJSONObject("data");
    req.getSession()
        .setAttribute(CommonConstant.CAPTCHA_CODE_SESSION_KEY, dataJson.getStr("verifyCode"));
    String base64 = dataJson.getStr("verifyCodeBase64");
    // the ignored first part is data:image/jpg;base64
    base64 = base64.split(",")[1];
    byte[] imageBytes = Base64.getDecoder().decode(base64);
    writeResponse(resp, imageBytes);
  }

  private static Resource getRandomLocalStaticImage() {
    if (localStaticImages == null) {
      localStaticImages =
          SingleFlightUtil.execute(
              "localStaticImages",
              () ->
                  new PathMatchingResourcePatternResolver()
                      .getResources("classpath:static_captcha/*"));
    }
    return localStaticImages[RandomUtil.randomInt(0, localStaticImages.length)];
  }

  private static byte[] resourceToBytes(Resource resource) throws IOException {
    try (InputStream inputStream = resource.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      byte[] buffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }
      return outputStream.toByteArray();
    }
  }

  private static String readContent(String path) {
    if (!Paths.get(path).isAbsolute()) {
      path = System.getProperty("user.home") + File.separator + path;
    }
    path = StringUtils.cleanPath(path);
    if (FileUtil.exists(path)) {
      return FileUtil.readUtf8String(path).trim();
    }
    return null;
  }
}
