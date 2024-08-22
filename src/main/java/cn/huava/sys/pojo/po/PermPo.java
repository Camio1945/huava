package cn.huava.sys.pojo.po;

import cn.huava.common.pojo.po.BasePo;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 权限
 *
 * @author Camio1945
 */
@Data
@TableName("sys_perm")
public class PermPo extends BasePo {

  /** 类型：M-菜单（用户是否能看到该页面），E-元素（用户是否能看到页面上的某个元素） */
  private String type;

  /** 名称 */
  private String name;

  /** 父菜单ID：0-一级菜单，其他值-父菜单ID */
  private Long parentId;

  /** 显示顺序：由低到高 */
  private Integer orderNum;

  /** 请求地址 */
  private String url;

  /** 打开方式：C-当前窗口（Current），B-新窗口（Blank） */
  private String target;

  /** 是否启用 */
  private Boolean isEnabled;

  /** 图标 */
  private String icon;

  /** 标识：如 sys:user:page */
  private String mark;
}
