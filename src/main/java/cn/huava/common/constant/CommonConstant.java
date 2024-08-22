package cn.huava.common.constant;

import cn.huava.common.validation.Create;
import cn.huava.common.validation.Update;

/**
 * About @SuppressWarnings("java:S1214"): <br>
 * This rule raises an issue when an interface consists only of constant definitions without other
 * members.<br>
 * But this class intends to be a constant class, it does not need methods.
 *
 * @author Camio1945
 */
@SuppressWarnings("java:S1214")
public interface CommonConstant {
  long MILLIS_PER_SECOND = 1000L;
  String CAPTCHA_CODE_SESSION_KEY = "captchaCode";
  String ENV_PROD = "prod";
  String ENV_PRODUCTION = "production";
  String AUTHORIZATION_HEADER = "Authorization";
  int MAX_PAGE_SIZE = 500;
  long ADMIN_ROLE_ID = 1L;
}
