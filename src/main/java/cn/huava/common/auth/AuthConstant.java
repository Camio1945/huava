package cn.huava.common.auth;

/**
 * Constants for authentication and authorization. <br>
 * About @SuppressWarnings: Without this annotation, SonarLint will warn: <br>
 * This rule raises an issue when an interface consists only of constant definitions without other
 * members.
 *
 * @author Camio1945
 */
@SuppressWarnings("java:S1214")
public interface AuthConstant {
  String REGISTERED_CLIENT_ID = "client";
  String USERNAME = "username";
  String PASSWORD = "password";
  String GRANT_TYPE = "grant_type";
  String SYS_PASSWORD_GRANT_TYPE = "sys_password";
  String REFRESH_TOKEN = "refresh_token";
  String ACCESS_TOKEN = "access_token";
  String AUTHORIZATION = "Authorization";
  String LOGIN_URI = "/sys/user/login";
  String REFRESH_TOKEN_URI = "/sys/user/refreshToken";
}
