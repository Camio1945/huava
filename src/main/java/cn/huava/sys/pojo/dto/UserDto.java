package cn.huava.sys.pojo.dto;

import cn.huava.sys.pojo.po.UserPo;
import java.util.List;
import lombok.Data;
import cn.hutool.v7.core.bean.BeanUtil;

/**
 * 用户 DTO
 *
 * @author Camio1945
 */
@Data
public class UserDto extends UserPo {

  /** 用户所拥有的角色 id 列表 */
  private List<Long> roleIds;

  public UserDto(UserPo po) {
    BeanUtil.copyProperties(po, this);
  }

  /** 不能显示密码 */
  @Override
  public String getPassword() {
    return "";
  }
}
