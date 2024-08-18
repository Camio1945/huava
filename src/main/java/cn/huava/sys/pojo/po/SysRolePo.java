package cn.huava.sys.pojo.po;

import cn.huava.common.pojo.po.BasePo;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色
 *
 * @author Camio1945
 */
@Data
@TableName("sys_role")
public class SysRolePo extends BasePo {
  /** 名称 */
  private String name;

  /** 描述 */
  private String description;
}
