package cn.huava.common.graalvm;

import static org.dromara.hutool.core.io.file.FileNameUtil.EXT_JAVA;

import cn.huava.common.util.Fn;
import java.io.File;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.io.file.FileUtil;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.thread.ThreadUtil;
import org.dromara.hutool.json.JSONArray;
import org.dromara.hutool.json.JSONObject;

/**
 * 生成 serialization-config.json 文件，主要作用是把本项目中用到了 lambda 表达式的类都注册到该文件的 lambdaCapturingTypes 属性里，用于
 * GraalVM native image。<br>
 * 文件路径 : src/main/resources/META-INF/native-image/serialization-config.json
 *
 * @author Camio1945
 */
@Slf4j
public class SerializationConfigGenerator {
  /** 源代码 main 目录的路径，如：D:/git/huava/src/main */
  protected static String mainPath;

  /** serialization-config.json 文件路径 */
  protected static String jsonFilePath;

  protected SerializationConfigGenerator() {}

  /**
   * 生成 serialization-config.json 文件。<br>
   * 该方法在 java -jar 或 GraalVM native image 环境下不会起作用，仅用于开发阶段或 native 编译阶段。
   */
  public static void generateSerializationConfigFile() {
    ThreadUtil.execute(
        () -> {
          if (Fn.isInJar() || Fn.isInGraalVmNativeImage()) {
            return;
          }
          initMainPath();
          List<File> files =
              FileUtil.loopFiles(new File(mainPath), f -> f.getName().endsWith(EXT_JAVA));
          String serializationConfigJson = buildSerializationConfigJson(files);
          writeJsonToFileIfChanged(serializationConfigJson);
        });
  }

  /** Return the project's main folder path, e.g. : D:/git/huava/src/main */
  private static void initMainPath() {
    String path = buildMainPathDynamically();
    Assert.isTrue(new File(path).exists(), "Cannot find the source code path: " + path);
    mainPath = Fn.cleanPath(path);
  }

  private static String buildSerializationConfigJson(List<File> files) {
    JSONObject json = new JSONObject();
    json.put("types", new JSONArray());
    json.put("proxies", new JSONArray());
    json.put("lambdaCapturingTypes", buildLambdaCapturingTypes(files));
    return json.toStringPretty();
  }

  private static void writeJsonToFileIfChanged(String serializationConfigJson) {
    jsonFilePath = mainPath + "/resources/META-INF/native-image/serialization-config.json";
    if (Fn.exists(jsonFilePath)
        && FileUtil.readUtf8String(jsonFilePath).equals(serializationConfigJson)) {
      log.debug("serialization-config.json 文件内容是最新的，不需要重新生成");
      return;
    }
    FileUtil.writeUtf8String(serializationConfigJson, jsonFilePath);
    log.debug("serialization-config.json 文件已重新生成");
  }

  private static String buildMainPathDynamically() {
    String path =
        SerializationConfigGenerator.class
            .getProtectionDomain()
            .getCodeSource()
            .getLocation()
            .getPath();
    String target = "/target/";
    path = path.substring(0, path.indexOf(target));
    return path + "/src/main";
  }

  private static JSONArray buildLambdaCapturingTypes(List<File> files) {
    JSONArray lambdas = new JSONArray();
    for (File file : files) {
      String content = FileUtil.readUtf8String(file);
      // 会有误判，比如当前类就没有用到 lambda 表达式，但也会算进去。但只会多文件，不会少文件，因此没有问题。
      if (!content.contains("::") && !content.contains("->")) {
        continue;
      }
      addLambdaClass(file, lambdas);
    }
    return lambdas;
  }

  private static void addLambdaClass(File file, JSONArray lambdas) {
    JSONObject lambdaJson = new JSONObject();
    String path = Fn.cleanPath(file.getAbsolutePath());
    String srcMainJava = "/src/main/java/";
    path = path.substring(path.indexOf(srcMainJava) + srcMainJava.length());
    String className = path.replace("/", ".").replace(EXT_JAVA, "");
    lambdaJson.putValue("name", className);
    lambdas.add(lambdaJson);
  }
}
