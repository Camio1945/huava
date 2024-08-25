package cn.huava.sys.pojo.po;


import cn.huava.common.validation.*;
import cn.huava.sys.validation.user.*;

import java.io.Serial;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 用户
 *
 * @author Camio1945
 */
@Data
public class UserExtPo extends UserPo {
  @Serial
  private static final long serialVersionUID = 1L;
  @RoleIds(groups = {Create.class, Update.class})
  @TableField(exist = false)
  private List<Long> roleIds;
}
