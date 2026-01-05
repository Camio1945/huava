package cn.huava.common.util;

import org.graalvm.nativeimage.ImageInfo;

/**
 * 环境工具类
 *
 * @author Camio1945
 */
public class EnvironmentUtil {
  private EnvironmentUtil() {}

  /** 当前是否在 GraalVM native image 环境中运行 */
  public static boolean isInGraalVmNativeImage() {
    return ImageInfo.inImageRuntimeCode();
  }

  /** 当前是否在 jar 包中运行 */
  public static boolean isInJar() {
    String path =
        EnvironmentUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    return path.endsWith(".jar");
  }
}
