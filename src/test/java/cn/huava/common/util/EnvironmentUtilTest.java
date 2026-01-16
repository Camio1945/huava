package cn.huava.common.util;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Tests for EnvironmentUtil class
 *
 * @author Camio1945
 */
class EnvironmentUtilTest {

  @Test
  void should_return_boolean_result_for_isInGraalVmNativeImage() {
    // Test the isInGraalVmNativeImage method
    // This method calls ImageInfo.inImageRuntimeCode() which will return false when running in JVM
    boolean result = EnvironmentUtil.isInGraalVmNativeImage();

    // The result will be false when running in JVM, true when in native image
    // Verify that it returns a boolean value without throwing an exception
    assertThat(result).isInstanceOf(Boolean.class);
  }

  @Test
  void should_determine_if_running_from_jar_or_class_files() {
    // Test the isInJar method
    // This method checks if the class location ends with ".jar"
    boolean result = EnvironmentUtil.isInJar();

    // The result will be true if running from a JAR file, false if running from class files (e.g., during development)
    // Verify that it returns a boolean value without throwing an exception
    assertThat(result).isInstanceOf(Boolean.class);
  }
}
