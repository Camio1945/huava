package cn.huava.sys.pojo.qo;

/**
 * login parameters
 *
 * @author Camio1945
 */
public record LoginQo(
    String username, String password, String captchaCode, Boolean isCaptchaDisabledForTesting) {}
