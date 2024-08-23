package cn.huava.common.graalvm;

import cn.huava.common.util.Fn;
import java.io.File;
import java.util.List;
import lombok.NonNull;
import org.dromara.hutool.core.io.file.FileUtil;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.json.JSONArray;
import org.dromara.hutool.json.JSONObject;

/**
 * Generate the serialization-config.json file for GraalVM native image.<br>
 * File path : src/main/resources/META-INF/native-image/serialization-config.json
 *
 * @author Camio1945
 */
class SerializationConfigGenerator {
  private static final String HUAVA_PATH = "";

  public static void main(String[] args) {
    String mainPath = getMainPath();
    List<File> files = FileUtil.loopFiles(new File(mainPath), f -> f.getName().endsWith(".java"));
    String serializationConfigJson = buildSerializationConfigJson(files);
    writeJsonToFileIfChanged(serializationConfigJson, mainPath);
  }

  /** Return the project's main folder path, e.g. : D:/git/huava/src/main */
  private static @NonNull String getMainPath() {
    String mainPath;
    if (Fn.isNotBlank(HUAVA_PATH)) {
      mainPath = HUAVA_PATH + "/src/main";
    } else {
      mainPath = buildMainPathDynamically();
    }
    Assert.isTrue(new File(mainPath).exists(), "Cannot find the source code path: " + mainPath);
    return Fn.cleanPath(mainPath);
  }

  private static String buildSerializationConfigJson(List<File> files) {
    JSONObject json = new JSONObject();
    json.put("types", new JSONArray());
    json.put("proxies", new JSONArray());
    json.put("lambdaCapturingTypes", buildLambdaCapturingTypes(files));
    return json.toStringPretty();
  }

  private static void writeJsonToFileIfChanged(String serializationConfigJson, String mainPath) {
    String jsonFilePath = mainPath + "/resources/META-INF/native-image/serialization-config.json";
    if (FileUtil.readUtf8String(jsonFilePath).equals(serializationConfigJson)) {
      return;
    }
    FileUtil.writeUtf8String(serializationConfigJson, jsonFilePath);
  }

  private static String buildMainPathDynamically() {
    String path =
        SerializationConfigGenerator.class
            .getProtectionDomain()
            .getCodeSource()
            .getLocation()
            .getPath();
    String target = "/target/";
    Assert.isTrue(
        path.contains(target),
        "Cannot get the source code path automatically, please set the huavaPath by hand. For example:\n"
            + "private static final String HUAVA_PATH = \"D:/git/huava\";");
    path = path.substring(0, path.indexOf(target));
    return path + "/src/main";
  }

  private static JSONArray buildLambdaCapturingTypes(List<File> files) {
    JSONArray lambdas = new JSONArray();
    for (File file : files) {
      if (!FileUtil.readUtf8String(file).contains("::")) {
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
    String className = path.replace("/", ".").replace(".java", "");
    lambdaJson.put("name", className);
    lambdas.add(lambdaJson);
  }
}
