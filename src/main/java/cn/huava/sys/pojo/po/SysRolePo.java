package cn.huava.sys.pojo.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * system role
 *
 * @author Camio1945
 */
@Data
@TableName("sys_role")
public class SysRolePo implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  @TableId private Long id;

  private String name;
}
