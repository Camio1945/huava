package cn.huava.sys.pojo.po;

import cn.huava.common.pojo.po.BasePo;
import cn.huava.common.validation.*;
import cn.huava.sys.validation.role.*;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 角色
 *
 * @author Camio1945
 */
@Data
@TableName("sys_role")
@UniqueRoleName(
    message = "角色名称已存在",
    groups = {Create.class, Update.class})
@BeforeDeleteRole(groups = {Delete.class})
@BeforeUpdateRole(groups = {Update.class})
public class RolePo extends BasePo {
  /** 名称 */
  @NotBlank(
      message = "名称不能为空",
      groups = {Create.class, Update.class})
  @Size(
      min = 2,
      max = 20,
      message = "名称长度应该为 2 ~ 20 个字符",
      groups = {Create.class, Update.class})
  private String name;

  /** 排序 */
  @NotNull(
      message = "排序不能为空",
      groups = {Create.class, Update.class})
  private Integer sort;

  /** 描述 */
  @Size(
      max = 200,
      message = "描述长度不能超过 200 个字符",
      groups = {Create.class, Update.class})
  private String description;
}
