package cn.huava.sys.pojo.dto;

import lombok.Data;

/**
 * Used for api: /sys/user/login
 *
 * @author Camio1945
 */
@Data
public class UserJwtDto {
  private String accessToken;
  private String refreshToken;
}
