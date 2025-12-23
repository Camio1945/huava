package cn.huava.common.util;

import io.github.humbleui.skija.*;
import io.github.humbleui.types.Rect;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Captcha utility using Skija for image generation
 *
 * @author Camio1945
 * @since 2025-12-23
 */
public class SkijaCaptchaUtil {
    
    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final Random RANDOM = new Random();
    
    public static class CaptchaResult {
        private final String code;
        private final BufferedImage image;
        
        public CaptchaResult(String code, BufferedImage image) {
            this.code = code;
            this.image = image;
        }
        
        public String getCode() {
            return code;
        }
        
        public BufferedImage getImage() {
            return image;
        }
    }
    
    /**
     * Generate a captcha with specified dimensions
     * @param width Width of the captcha image
     * @param height Height of the captcha image
     * @param codeLength Length of the captcha code
     * @return CaptchaResult containing the code and image
     */
    public static CaptchaResult generateCaptcha(int width, int height, int codeLength) {
        String code = generateRandomCode(codeLength);
        
        // Create Skija surface
        Surface surface = Surface.makeRasterN32Premul(width, height);
        Canvas canvas = surface.getCanvas();
        
        // Fill background
        canvas.clear(0xFFFFFFFF);
        
        // Draw random lines
        drawRandomLines(canvas, width, height);
        
        // Draw text
        drawText(canvas, code, width, height);
        
        // Get the image as BufferedImage
        Image skijaImage = surface.makeImageSnapshot();
        BufferedImage bufferedImage = skijaToBufferedImage(skijaImage, width, height);
        
        surface.close();
        skijaImage.close();
        
        return new CaptchaResult(code, bufferedImage);
    }
    
    private static String generateRandomCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
    
    private static void drawRandomLines(Canvas canvas, int width, int height) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        
        for (int i = 0; i < 8; i++) {
            paint.setColor(Color.makeRGB(RANDOM.nextInt(150), RANDOM.nextInt(150), RANDOM.nextInt(150)));
            float startX = RANDOM.nextInt(width);
            float startY = RANDOM.nextInt(height);
            float endX = RANDOM.nextInt(width);
            float endY = RANDOM.nextInt(height);
            canvas.drawLine(startX, startY, endX, endY, paint);
        }
        
        paint.close();
    }
    
    private static void drawText(Canvas canvas, String text, int width, int height) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        
        // Set up font
        Font font = new Font(Typeface.makeDefault(), height * 0.7f);
        
        // Draw each character with random rotation and position
        float charWidth = width / (float) text.length();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String charStr = String.valueOf(c);
            
            // Random color for each character
            paint.setColor(Color.makeRGB(RANDOM.nextInt(100), RANDOM.nextInt(100), RANDOM.nextInt(100)));
            
            // Calculate position with some randomness
            float x = i * charWidth + (charWidth - getTextWidth(font, charStr)) / 2 + RANDOM.nextInt(5) - 2;
            float y = height / 2f + (getTextHeight(font) / 2f) - 5 + RANDOM.nextInt(5) - 2;
            
            // Apply random rotation
            canvas.save();
            canvas.translate(x, y);
            canvas.rotate(RANDOM.nextInt(20) - 10);
            canvas.drawString(charStr, 0, 0, font, paint);
            canvas.restore();
        }
        
        paint.close();
        font.close();
    }
    
    private static float getTextWidth(Font font, String text) {
        Rect bounds = font.measureText(text);
        return bounds != null ? bounds.getWidth() : 0;
    }
    
    private static float getTextHeight(Font font) {
        FontMetrics metrics = font.getMetrics();
        return metrics.getCapHeight();
    }
    
    private static BufferedImage skijaToBufferedImage(Image image, int width, int height) {
        // Create a surface to render the image to
        try (Surface surface = Surface.makeRasterN32Premul(width, height)) {
            Canvas canvas = surface.getCanvas();
            // Draw the image to the surface
            canvas.drawImage(image, 0, 0);

            // Encode the surface to PNG data
            try (Data data = surface.makeImageSnapshot().encodeToData()) {
                if (data != null) {
                    // Convert the Skija data to a byte array
                    byte[] bytes = data.getBytes();
                    if (bytes != null) {
                        // Read the byte array as an image using ImageIO
                        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
                            return ImageIO.read(inputStream);
                        } catch (Exception e) {
                            // If reading fails, return an empty BufferedImage
                            System.err.println("Error reading captcha image: " + e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error converting Skija image to BufferedImage: " + e.getMessage());
        }

        // Fallback: return empty image if conversion fails
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }
}