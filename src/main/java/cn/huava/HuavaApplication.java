package cn.huava;

import java.io.Serializable;

import cn.huava.common.annotation.UnreachableForTesting;
import cn.huava.common.graalvm.SerializationConfigGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动器
 *
 * @author Camio1945
 */
@UnreachableForTesting("启动类在单元测试时用不到。 Spring 在单元测试时好像另有一套启动机制。")
@SpringBootApplication
public class HuavaApplication implements Serializable {

  public static void main(String[] args) {
    // 为了避免不必要的麻烦，以下这行代码请不要删除，具体功能见方法本身的注释
    SerializationConfigGenerator.generateSerializationConfigFile();
    SpringApplication.run(HuavaApplication.class, args);
  }
}
