package cn.huava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

///
/// # Main application class for the Huava project
///
@SpringBootApplication
public class HuavaApplication {
  private HuavaApplication() {}

  static void main(String[] args) {
    SpringApplication.run(HuavaApplication.class, args);
  }
}
