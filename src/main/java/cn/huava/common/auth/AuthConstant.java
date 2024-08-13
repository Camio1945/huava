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
  String USERNAME = "username";
  String PASSWORD = "password";
  String SYS_PASSWORD_GRANT_TYPE = "sys_password";
  String ACCESS_TOKEN = "access_token";
  String AUTHORIZATION = "Authorization";
  String BEARER_PREFIX = "Bearer ";
  String LOGIN_URI = "/sys/user/login";
}
