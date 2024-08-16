package cn.huava.sys.pojo.dto;

import lombok.Data;

/**
 * @author Camio1945
 */
@Data
public class SysUserJwtDto {
  private String accessToken;
  private String refreshToken;
}
