package cn.huava.sys.pojo.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * 角色拥有的权限
 *
 * @author Camio1945
 */
@Data
@TableName("sys_role_perm")
public class RolePermPo implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  /** 主键 */
  @TableId private Long id;

  /** 角色ID */
  private Long roleId;

  /** 权限ID */
  private Long permId;

  public RolePermPo() {}

  public RolePermPo(Long roleId, Long permId) {
    this.roleId = roleId;
    this.permId = permId;
  }
}
