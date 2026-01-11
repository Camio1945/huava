package cn.huava.common.util;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Camio1945
 */
@NullMarked
class EncryptUtil {
  private EncryptUtil() {}

  public static @Nullable String encryptPassword(final String str) {
    PasswordEncoder encoder = Fn.getBean(PasswordEncoder.class);
    return encoder.encode(str);
  }
}
