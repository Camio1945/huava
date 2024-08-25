package cn.huava.common.constant;


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
  long ADMIN_USER_ID = 1L;
  String REFRESH_TOKEN_URI = "/sys/user/refreshToken";
  String KEEP_ORIGINAL_PASSWORD = "KEEP_ORIGINAL_PASSWORD";
  int MIN_PASSWORD_LENGTH = 8;
  int MAX_PASSWORD_LENGTH = 20;

  interface RoleMessage {
    String IMPORTANT_ROLE = "该角色为最重要的基础角色，不允许进行任何操作";
    String IMPORTANT_USER = "该用户为最重要的基础用户，不允许进行任何操作";
  }
}
