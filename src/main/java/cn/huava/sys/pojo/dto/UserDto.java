package cn.huava.sys.pojo.dto;

import cn.huava.sys.pojo.po.UserPo;
import java.util.List;
import lombok.Data;
import org.dromara.hutool.core.bean.BeanUtil;

/**
 * 用户 DTO
 *
 * @author Camio1945
 */
@Data
public class UserDto extends UserPo {

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
