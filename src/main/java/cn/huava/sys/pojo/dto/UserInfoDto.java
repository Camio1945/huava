package cn.huava.sys.pojo.dto;

import java.util.List;
import lombok.Data;

/**
 * 用户信息，用于前端首页获取用户的菜单与权限
 *
 * @author Camio1945
 */
@Data
public class UserInfoDto {
  private String username;
  private String avatar;
  private List<PermDto> menu;
  private List<String> uris;
}
