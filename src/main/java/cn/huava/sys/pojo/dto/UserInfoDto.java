package cn.huava.sys.pojo.dto;

import java.util.List;
import lombok.Data;

/**
 * Used for api: /sys/user/info
 *
 * @author Camio1945
 */
@Data
public class UserInfoDto {
  private String username;
  private List<String> roles;

  public UserInfoDto() {}

  public UserInfoDto(String username, List<String> roles) {
    this.username = username;
    this.roles = roles;
  }
}
