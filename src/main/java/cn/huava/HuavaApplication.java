package cn.huava;

import cn.huava.common.annotation.UnreachableForTesting;
import cn.huava.common.graalvm.SerializationConfigGenerator;
import java.io.Serializable;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

/**
 * 启动器
 *
 * @author Camio1945
 */
@Slf4j
@UnreachableForTesting("启动类在单元测试时用不到。 Spring 在单元测试时好像另有一套启动机制。")
@SpringBootApplication
public class HuavaApplication implements Serializable {

  public static void main(String[] args) {
    // 为了避免不必要的麻烦，以下这行代码请不要删除，具体功能见方法本身的注释
    SerializationConfigGenerator.generateSerializationConfigFile();
    ConfigurableApplicationContext context = SpringApplication.run(HuavaApplication.class, args);
    printMsg(context);
  }

  /** 打印启动成功的消息 */
  private static void printMsg(ConfigurableApplicationContext context) {
    Environment env = context.getEnvironment();
    String port = env.getProperty("server.port");
    String path = env.getProperty("server.servlet.context-path");
    path = path == null ? (port) : (port + path);
    String msgTemplate =
        """


    ----------------------------------------------------------
    Application is running! Access URLs:
    Local: \t\thttp://localhost:{}/
    swagger: \thttp://localhost:{}/doc.html （TODO）
    ------------------------------------------------------------

    """;
    String msg = StrUtil.format(msgTemplate, path, path);
    log.info(msg);
  }
}
