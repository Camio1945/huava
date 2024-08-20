package cn.huava.common.graalvm;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.io.file.FileUtil;
import org.dromara.hutool.json.*;

/**
 * Used to generate code from config files. <br>
 * 1. The config files comes from <a
 * href="https://www.graalvm.org/latest/reference-manual/native-image/metadata/AutomaticMetadataCollection/">agentlib</a>.
 * <br>
 * 2. the config file folder should be named native-image-config and reside in %user.home%/.huava
 * folder. <br>
 * Windows example:
 *
 * <pre>
 *    C:\Users\Administrator\.huava\native-image-config
 *        jni-config.json
 *        predefined-classes-config.json
 *        proxy-config.json
 *        reflect-config.json
 *        serialization-config.json
 * </pre>
 *
 * @author Camio1945
 */
@Slf4j
public class NativeHintsRegistrarGenerator {
  /** e.g. C:\Users\Administrator\.huava\native-image-config */
  private static final String BASE_PATH =
      System.getProperty("user.home")
          + File.separator
          + ".huava"
          + File.separator
          + "native-image-config";

  private static final String REFLECT_CONFIG_FILE_PATH =
      BASE_PATH + File.separator + "reflect-config.json";
  private static final Set<String> NOT_COMPILABLE_CLASS_NAMES =
      Set.of(
          "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl",
          "com.sun.org.apache.xerces.internal.jaxp.datatype.DatatypeFactoryImpl",
          "jakarta.activation.MimeType",
          "jakarta.xml.bind.Binder",
          "jdk.internal.misc.Unsafe",
          "org.springframework.boot.context.config.DelegatingApplicationContextInitializer",
          "org.springframework.boot.context.config.DelegatingApplicationListener",
          "sun.java2d.marlin.DMarlinRenderingEngine",
          "sun.security.provider.NativePRNG",
          "sun.security.provider.SHA",
          "sun.security.provider.SecureRandom",
          "sun.security.provider.X509Factory");

  public static void main(String[] args) {
    if (!FileUtil.exists(REFLECT_CONFIG_FILE_PATH)) {
      log.warn("reflect-config.json not found in {}", BASE_PATH);
      return;
    }
    String content = FileUtil.readUtf8String(REFLECT_CONFIG_FILE_PATH);
    JSONArray jsonArray = JSONUtil.parseArray(content);
    JSONArray remainArray = new JSONArray();
    for (Object o : jsonArray) {
      JSONObject json = (JSONObject) o;
      String className = json.getStr("name");
      if ((className.startsWith("cn.huava")
              && (className.endsWith("Mapper")
                  || className.endsWith("Service")
                  || className.endsWith("Service$$SpringCGLIB$$0")
                  || className.endsWith("Controller")))
          || className.endsWith("Mapper")
          || className.startsWith("com.zaxxer.hikari")
          || className.startsWith("org.springframework")
          || className.startsWith("org.apache.catalina")
          || className.startsWith("com.baomidou.mybatisplus.extension.service.IService")
          || className.startsWith("org.mybatis.spring.SqlSessionTemplate")
          || className.startsWith("com.fasterxml.jackson.databind.module.SimpleModule")
          || className.startsWith("ch.qos.logback.classic")
          || className.startsWith("sun")
          || className.startsWith("org.mybatis.spring")
          || className.startsWith("org.apache.ibatis.session")
          || className.startsWith("cn.huava.sys.pojo.po.SysUserPo")
          || className.startsWith("java.sql")
          || className.startsWith("java.lang")
          || className.startsWith("javax")
          || className.startsWith("jakarta")
          || className.startsWith("com.baomidou.mybatisplus.extension.service.impl.ServiceImpl")
          || className.startsWith("com.mysql.cj")
          || className.startsWith("com.sun.org.apache.xerces.internal.jaxp")
          || className.startsWith("java.nio")
          || className.startsWith("java.net")
          || className.startsWith("boolean")
          || className.startsWith("com.fasterxml.jackson.databind.JsonSerializer")
          || className.startsWith("cn.huava.sys.pojo")
          || className.startsWith("cn.huava.common.pojo.dto.ApiResponseDataDto")
          || className.startsWith("com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties")
          || className.startsWith("com.fasterxml.jackson.module.paramnames.ParameterNamesModule")
          || className.startsWith("com.fasterxml.jackson.databind.ser.std.ToStringSerializerBase")
          || className.startsWith("com.fasterxml.jackson.databind.ser.std.StdSerializer")
          || className.startsWith("com.fasterxml.jackson.core.ObjectCodec")
          || className.startsWith("com.fasterxml.jackson.databind.Module")
          || className.startsWith("com.fasterxml.jackson.databind.ext.Java7SupportImpl")
          || className.startsWith("com.fasterxml.jackson.datatype.jdk8.Jdk8Module")
          || className.startsWith("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule")
          || className.startsWith("java.io.Console")
          || className.startsWith("java.util.HashMap")
          || className.startsWith("java.util.concurrent.Callable")
          || className.startsWith("java.util.logging.SimpleFormatter")
          || className.startsWith("javax.imageio.spi.ImageReaderWriterSpi")
          || className.startsWith("com.baomidou.mybatisplus.autoconfigure.DdlAutoConfiguration")
          || className.startsWith("com.baomidou.mybatisplus.autoconfigure.MybatisDependsOnDatabaseInitializationDetector")
          || className.startsWith("com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration")
          || className.startsWith("com.baomidou.mybatisplus.autoconfigure.SafetyEncryptProcessor")
          || className.startsWith("org.apache.tomcat.util.net.AbstractEndpoint")
          || className.startsWith("org.dromara.hutool.extra.spring.SpringUtil")
          || className.startsWith("org.graalvm.nativeimage.ImageInfo")
          || className.startsWith("cn.huava.HuavaApplication")
          || className.startsWith("cn.huava.common.config.SecurityConfig")
          || className.startsWith("cn.huava.common.controller.handler.GlobalExceptionHandler")
          || className.startsWith("cn.huava.common.filter.JwtAuthenticationFilter")
          || className.startsWith("cn.huava.common.graalvm.RuntimeHintsRegistrarConfig")
          || className.startsWith("cn.huava.sys.service.SysUserLoginUserDetailsServiceImpl")
          || className.contains("$")
      ) {
        continue;
      }
      remainArray.add(json);
    }
    String remainJsonStr = remainArray.toStringPretty();
    // String remainPath = BASE_PATH + File.separator + "reflect-config-remain.json";
    String remainPath = "F:/temp/reflect-config.json";
    FileUtil.writeUtf8String(remainJsonStr, remainPath);
    // backup();
  }

  private static void backup() {
    List<String> classNames = new ArrayList<>();
    String content = FileUtil.readUtf8String(REFLECT_CONFIG_FILE_PATH);
    JSONArray jsonArray = JSONUtil.parseArray(content);
    JSONArray remainArray = new JSONArray();
    for (Object o : jsonArray) {
      JSONObject json = (JSONObject) o;
      String className = json.getStr("name");
      if (shouldRemainInJsonFile(className)) {
        remainArray.add(json);
        continue;
      }
      classNames.add(className);
    }
    FileUtil.writeUtf8Lines(classNames, "E:/test.txt");

    String remainJsonStr = remainArray.toStringPretty();
    String remainPath = BASE_PATH + File.separator + "reflect-config-remain.json";
    FileUtil.writeUtf8String(remainJsonStr, remainPath);
    log.info("reflect-config-remain.json generated in {}", remainPath);
  }

  private static boolean shouldRemainInJsonFile(String className) {
    return className.startsWith("[")
        || !className.contains(".")
        || className.contains("$")
        || !isClassPublic(className)
        || NOT_COMPILABLE_CLASS_NAMES.contains(className);
  }

  private static boolean isClassPublic(String className) {
    try {
      Class<?> clazz = Class.forName(className);
      return Modifier.isPublic(clazz.getModifiers());
    } catch (Throwable ignore) {
      return false;
    }
  }
}
