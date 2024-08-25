package cn.huava.sys.pojo.dto;

import static cn.huava.common.constant.CommonConstant.KEEP_ORIGINAL_PASSWORD;

import cn.huava.sys.pojo.po.UserPo;
import lombok.Data;
import org.dromara.hutool.core.bean.BeanUtil;

/**
 * Used for api: /sys/user/info
 *
 * @author Camio1945
 */
@Data
public class UserDto extends UserPo {
  public UserDto(UserPo po) {
    BeanUtil.copyProperties(po, this);
  }

  @Override
  public String getPassword() {
    return KEEP_ORIGINAL_PASSWORD;
  }
}
