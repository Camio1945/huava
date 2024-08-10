package cn.huava.sys.pojo.dto;

import lombok.Data;

/**
 * @author Camio1945
 */
@Data
public class SysUserJwtDto {
  private String accessToken;

  public SysUserJwtDto(String accessToken) {
    this.accessToken = accessToken;
  }
}
