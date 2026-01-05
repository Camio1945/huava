package cn.huava.sys.pojo.po;

import cn.huava.common.pojo.po.BasePo;
import cn.huava.common.validation.Create;
import cn.huava.common.validation.Update;
import cn.huava.sys.enumeration.PermTypeEnum;
import cn.huava.common.validation.ValidEnum;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 权限
 *
 * @author Camio1945
 */
@Data
@TableName("sys_perm")
public class PermPo extends BasePo {

  /** 上级权限ID */
  private Long pid;

  /** 权限类型: D=目录(Directory)，M=菜单(Menu)，E=元素(Element) */
  @ValidEnum(
      enumClass = PermTypeEnum.class,
      message = "权限类型的可选值只能为：D、M、E",
      groups = {Create.class, Update.class})
  private String type;

  /** 名称 */
  @NotEmpty(
      message = "权限名称不能为空",
      groups = {Create.class, Update.class})
  private String name;

  /** 图标 */
  private String icon;

  /** 排序 */
  @NotNull(
      message = "权限排序不能为空",
      groups = {Create.class, Update.class})
  private Integer sort;

  /** 接口URI */
  private String uri;

  /** 路由地址 */
  private String paths;

  /** 前端组件 */
  private String component;

  /** 选中路径 */
  private String selected;

  /** 路由参数 */
  private String params;

  /** 是否缓存 */
  private Boolean isCache;

  /** 是否显示 */
  private Boolean isShow;

  /** 是否启用 */
  private Boolean isEnabled;
}
