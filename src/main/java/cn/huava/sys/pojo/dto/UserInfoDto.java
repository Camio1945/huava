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
  private String avatar;
  private List<PermDto> menu;
}
