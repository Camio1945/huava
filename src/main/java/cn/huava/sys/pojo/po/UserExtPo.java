package cn.huava.sys.pojo.po;

import cn.huava.common.validation.*;
import cn.huava.sys.validation.user.*;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serial;
import java.util.List;
import lombok.Data;

/**
 * 用户扩展对象，增加了用户所属的角色 id 列表
 *
 * @author Camio1945
 */
@Data
public class UserExtPo extends UserPo {
  @Serial private static final long serialVersionUID = 1L;

  @RoleIds(groups = {Create.class, Update.class})
  @TableField(exist = false)
  private List<Long> roleIds;
}
