package cn.huava.common.util;

import java.security.*;
import java.security.spec.*;
import java.util.Base64;
import lombok.SneakyThrows;

/**
 * @author Camio1945
 */
public class KeyUtil {
  private KeyUtil() {}

  public static KeyPair getKeyPair(String base64PublicKey, String base64PrivateKey) {
    PublicKey publicKey = getPublicKeyFromBase64(base64PublicKey);
    PrivateKey privateKey = getPrivateKeyFromBase64(base64PrivateKey);
    return new KeyPair(publicKey, privateKey);
  }

  @SneakyThrows
  public static PublicKey getPublicKeyFromBase64(String base64PublicKey) {
    byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
    KeySpec keySpec = new X509EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    return keyFactory.generatePublic(keySpec);
  }

  @SneakyThrows
  public static PrivateKey getPrivateKeyFromBase64(String base64PrivateKey) {
    byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
    KeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    return keyFactory.generatePrivate(keySpec);
  }
}
