package cn.huava.sys.pojo.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * 用户拥有的角色
 *
 * @author Camio1945
 */
@Data
@TableName("sys_user_role")
public class UserRolePo implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  /** 主键 */
  @TableId private Long id;

  /** 用户ID */
  private Long userId;

  /** 角色ID */
  private Long roleId;
}
