package cn.huava.common.util;

import java.security.SecureRandom;

/**
 * Native-compatible captcha utility.
 * This is a replacement for hutool's AWT-dependent captcha implementation.
 *
 * @author Camio1945
 */
public class CaptchaUtil {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    /**
     * Generates a simple captcha code
     */
    public static CaptchaResult generateCaptcha(int codeLength) {
        StringBuilder captchaCode = new StringBuilder();

        // Generate random code
        for (int i = 0; i < codeLength; i++) {
            captchaCode.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return new CaptchaResult(captchaCode.toString());
    }

    /**
     * Gets the shared random generator for consistent randomness across the app
     */
    public static SecureRandom getRandom() {
        return random;
    }

    /**
     * Result class for captcha generation
     */
    public static class CaptchaResult {
        private final String code;

        public CaptchaResult(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }
}