package cn.huava.sys.pojo.qo;

import lombok.Data;

/**
 * login parameters
 *
 * @author Camio1945
 */
@Data
public class LoginQo {
  private String username;
  private String password;
  private String captchaCode;
  private Boolean isCaptchaDisabledForTesting;
}
