package cn.huava.common.util;

import io.github.humbleui.skija.*;
import io.github.humbleui.types.Rect;
import java.util.Random;

/**
 * Captcha utility using Skija for image generation
 *
 * @author Camio1945
 * @since 2025-12-23
 */
public class SkijaCaptchaUtil {
  private static final String CHARACTERS = "ABCDEFGHIJKLMNPQRSTUVWXYZabcdefghijklmnpqrstuvwxyz0123456789";
  private static final Random RANDOM = new Random();

  private SkijaCaptchaUtil() {
  }

  /**
   * Generate a captcha with specified dimensions
   *
   * @param width      Width of the captcha image
   * @param height     Height of the captcha image
   * @param codeLength Length of the captcha code
   * @return CaptchaResult containing the code and image
   */
  public static CaptchaResult generateCaptcha(int width, int height, int codeLength) {
    String code = generateRandomCode(codeLength);

    // Create Skija surface and image within try-with-resources
    ImageInfo imageInfo = ImageInfo.makeN32Premul(width, height);
    try (Surface surface = Surface.makeRaster(imageInfo)) {
      Canvas canvas = surface.getCanvas();

      // Fill background with a slight off-white color
      canvas.clear(0xFFF5F5F5);

      // Add background noise
      addBackgroundNoise(canvas, width, height);

      // Draw random lines
      drawRandomLines(canvas, width, height);

      // Draw text
      drawText(canvas, code, width, height);

      // Get the image as byte array
      try (Image image = surface.makeImageSnapshot();
           Data data = image.encodeToData(EncodedImageFormat.PNG)) {
        return new CaptchaResult(code, data.getBytes());
      }
    }
  }

  private static String generateRandomCode(int length) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
    }
    return sb.toString();
  }

  private static void addBackgroundNoise(Canvas canvas, int width, int height) {
    try (Paint paint = new Paint()) {
      // Add random noise dots
      // Adjust density as needed
      for (int i = 0; i < width * height / 10; i++) {
        int x = RANDOM.nextInt(width);
        int y = RANDOM.nextInt(height);
        // Light gray dots
        int grayValue = RANDOM.nextInt(50) + 200;
        paint.setColor(Color.makeRGB(grayValue, grayValue, grayValue));
        canvas.drawPoint(x, y, paint);
      }
    }
  }

  private static void drawRandomLines(Canvas canvas, int width, int height) {
    try (Paint paint = new Paint()) {
      paint.setAntiAlias(true);

      // Draw more random lines for interference
      for (int i = 0; i < 15; i++) {
        paint.setColor(Color.makeRGB(RANDOM.nextInt(150), RANDOM.nextInt(150), RANDOM.nextInt(150)));
        float startX = RANDOM.nextInt(width);
        float startY = RANDOM.nextInt(height);
        float endX = RANDOM.nextInt(width);
        float endY = RANDOM.nextInt(height);
        canvas.drawLine(startX, startY, endX, endY, paint);
      }

      // Add some random circles for additional noise
      for (int i = 0; i < 10; i++) {
        paint.setColor(Color.makeRGB(RANDOM.nextInt(150), RANDOM.nextInt(150), RANDOM.nextInt(150)));
        float centerX = RANDOM.nextInt(width);
        float centerY = RANDOM.nextInt(height);
        float radius = RANDOM.nextInt(8) + 2;
        canvas.drawCircle(centerX, centerY, radius, paint);
      }

      // Add some random rectangles for additional noise
      for (int i = 0; i < 8; i++) {
        paint.setColor(Color.makeRGB(RANDOM.nextInt(150), RANDOM.nextInt(150), RANDOM.nextInt(150)));
        float x = RANDOM.nextInt(width);
        float y = RANDOM.nextInt(height);
        float rectWidth = RANDOM.nextInt(20) + 5;
        float rectHeight = RANDOM.nextInt(10) + 3;
        canvas.drawRect(Rect.makeXYWH(x, y, rectWidth, rectHeight), paint);
      }
    }
  }

  private static void drawText(Canvas canvas, String text, int width, int height) {
    try (Paint paint = new Paint()) {
      paint.setAntiAlias(true);

      // Set up font
      try (FontMgr fontMgr = FontMgr.getDefault();
           Typeface typeface = fontMgr.matchFamilyStyle("sans-serif", FontStyle.NORMAL);
           Font font = new Font(typeface, height * 0.7f)) {

        // Draw each character with random rotation and position
        float charWidth = width / (float) text.length();
        for (int i = 0; i < text.length(); i++) {
          char c = text.charAt(i);
          String charStr = String.valueOf(c);

          // Random color for each character
          paint.setColor(Color.makeRGB(RANDOM.nextInt(100), RANDOM.nextInt(100), RANDOM.nextInt(100)));

          // Calculate position with more randomness
          float x = i * charWidth + (charWidth - getTextWidth(font, charStr)) / 2 + RANDOM.nextInt(10) - 5;
          float y = height / 2f + (getTextHeight(font) / 2f) - 5 + RANDOM.nextInt(10) - 5;

          // Apply more complex transformation - rotation and scaling
          canvas.save();
          canvas.translate(x, y);

          // More extreme rotation
          float rotation = RANDOM.nextInt(30) - 15;
          canvas.rotate(rotation);

          // Add slight scaling variation
          // Scale between 0.8 and 1.2
          float scaleX = 0.8f + RANDOM.nextFloat() * 0.4f;
          float scaleY = 0.8f + RANDOM.nextFloat() * 0.4f;
          canvas.scale(scaleX, scaleY);

          // Add wave distortion effect
          float waveOffset = (float) Math.sin(i) * 5;
          canvas.translate(0, waveOffset);

          canvas.drawString(charStr, 0, 0, font, paint);
          canvas.restore();
        }
      }
    }
  }

  private static float getTextWidth(Font font, String text) {
    Rect bounds = font.measureText(text);
    return bounds != null ? bounds.getWidth() : 0;
  }

  private static float getTextHeight(Font font) {
    FontMetrics metrics = font.getMetrics();
    return metrics.getCapHeight();
  }

  public record CaptchaResult(String code, byte[] image) {
  }
}
