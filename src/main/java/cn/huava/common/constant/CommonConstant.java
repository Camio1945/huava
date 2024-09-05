package cn.huava.common.constant;

/**
 * 关于 @SuppressWarnings("java:S1214"): <br>
 * 当接口只包含常量定义而不包含其他成员时，这个规则就会触发警告。但是当前那只是一个常量类，不需要有其他成员，因此这个警告可以忽略。
 *
 * @author Camio1945
 */
@SuppressWarnings("java:S1214")
public interface CommonConstant {
  /** 一秒有多少毫秒 */
  long MILLIS_PER_SECOND = 1000L;

  /** 验证码在 Session 中的 key */
  String CAPTCHA_CODE_SESSION_KEY = "captchaCode";

  /** 生产环境 */
  String ENV_PROD = "prod";

  /** 生产环境 */
  String ENV_PRODUCTION = "production";

  /** 认证请求头信息，对应的值就是 access token */
  String AUTHORIZATION_HEADER = "Authorization";

  /** 分页查询时最大允许每页多少条数据 */
  int MAX_PAGE_SIZE = 500;

  /** 超级管理员角色 id ，规范定死的，不要改 */
  long ADMIN_ROLE_ID = 1L;

  /** 超级管理员用户 id ，规范定死的，不要改 */
  long ADMIN_USER_ID = 1L;

  /** 刷新 token 对应的接口 uri 跑到 */
  String REFRESH_TOKEN_URI = "/sys/user/refreshToken";

  /** 密码最短的长度 */
  int MIN_PASSWORD_LENGTH = 8;

  /** 密码最长的长度 */
  int MAX_PASSWORD_LENGTH = 20;

  /** 上传文件时，表单中文件参数的名称 */
  String MULTIPART_PARAM_NAME = "file";

  /** JWT 令牌的前缀 */
  String BEARER_PREFIX = "Bearer ";

  interface RoleMessage {
    String IMPORTANT_ROLE = "该角色为最重要的基础角色，不允许进行任何操作";
    String IMPORTANT_USER = "该用户为最重要的基础用户，不允许进行任何操作";
  }
}
