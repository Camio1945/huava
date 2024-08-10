package cn.huava.sys.pojo.po;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * the relation between user and role
 *
 * @author Camio1945
 */
@Data
@TableName("sys_user_role")
public class SysUserRole implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  private Long userId;

  private Long roleId;
}
