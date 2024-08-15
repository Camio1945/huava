package cn.huava;

import java.io.Serializable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Starter
 *
 * @author Camio1945
 */
@SpringBootApplication(proxyBeanMethods = false)
public class HuavaApplication implements Serializable {

  public static void main(String[] args) {
    SpringApplication.run(HuavaApplication.class, args);
  }
}
