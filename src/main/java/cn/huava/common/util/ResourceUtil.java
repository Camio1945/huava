package cn.huava.common.util;

import java.io.*;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;

/**
 * 资源工具类
 *
 * @author Camio1945
 */
public class ResourceUtil {
  private ResourceUtil() {}

  /**
   * 将 {@link Resource} 转换为 byte[]
   *
   * @param resource {@link Resource}
   * @return `@SneakyThrows` 吞掉了 IOException
   */
  @SneakyThrows
  protected static byte[] resourceToBytes(Resource resource) {
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
}
