package cn.huava;

import java.io.Serializable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动器
 *
 * @author Camio1945
 */
@SpringBootApplication
public class HuavaApplication implements Serializable {

  public static void main(String[] args) {
    SpringApplication.run(HuavaApplication.class, args);
  }
}
