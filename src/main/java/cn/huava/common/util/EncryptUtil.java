package cn.huava.common.util;

import lombok.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Camio1945
 */
class EncryptUtil {
  private EncryptUtil() {}

  public static String encryptPassword(@NonNull final String str) {
    PasswordEncoder encoder = Fn.getBean(PasswordEncoder.class);
    return encoder.encode(str);
  }

}
