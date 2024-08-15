package cn.huava.common.util;

import lombok.NonNull;
import org.dromara.hutool.crypto.Mode;
import org.dromara.hutool.crypto.Padding;
import org.dromara.hutool.crypto.symmetric.AES;
import org.dromara.hutool.extra.spring.SpringUtil;
import org.springframework.core.env.Environment;

/**
 * @author Camio1945
 */
public class EncryptUtil {
  private static AES aes;

  private EncryptUtil() {}

  /**
   * Encrypt plainText with AES
   *
   * @param plainText string to be encrypted
   * @return encrypted string in base64
   */
  public static @NonNull String encrypt(@NonNull String plainText) {
    return getAes().encryptBase64(plainText);
  }

  /**
   * AES is a heavy object (take more than 1 second on my computer), so we use {@link
   * cn.huava.common.util.SingleFlightUtil} to avoid resource contention
   */
  private static AES getAes() {
    if (aes == null) {
      aes =
          SingleFlightUtil.execute(
              "init_aes",
              () -> {
                Environment env = SpringUtil.getBean(Environment.class);
                String aesCbcKey = env.getProperty("project.aes_cbc_key");
                String aesCbcIv = env.getProperty("project.aes_cbc_iv");
                assert aesCbcKey != null;
                assert aesCbcIv != null;
                return new AES(
                    Mode.CBC, Padding.PKCS5Padding, aesCbcKey.getBytes(), aesCbcIv.getBytes());
              });
    }
    return aes;
  }

  /**
   * Decrypt encryptedText with AES
   *
   * @param encryptedText encrypted string in base64
   * @return decrypted string
   */
  public static @NonNull String decrypt(@NonNull String encryptedText) {
    return getAes().decryptStr(encryptedText);
  }
}
