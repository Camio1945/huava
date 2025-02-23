package cn.huava.sys.pojo.qo;

import lombok.Data;

/**
 * 登录参数
 *
 * @author Camio1945
 */
@Data
public class LoginQo {
  /** 用户名 */
  private String username;

  /** 密码 */
  private String password;

  /** 图形验证码 */
  private String captchaCode;

  /** 是否禁用了验证码以方便测试（该参数仅在非生产环境下有效） */
  private Boolean isCaptchaDisabledForTesting;
}
