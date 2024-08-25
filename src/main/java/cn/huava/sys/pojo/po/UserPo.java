package cn.huava.sys.pojo.po;

import static cn.huava.common.constant.CommonConstant.MAX_PASSWORD_LENGTH;
import static cn.huava.common.constant.CommonConstant.MIN_PASSWORD_LENGTH;

import cn.huava.common.pojo.po.BasePo;
import cn.huava.common.validation.*;
import cn.huava.sys.validation.user.*;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.util.Date;
import lombok.Data;

/**
 * 用户
 *
 * @author Camio1945
 */
@Data
@TableName("sys_user")
@UniqueUsername(
    message = "用户名已存在",
    groups = {Create.class, Update.class})
@BeforeDeleteUser(groups = {Delete.class})
@BeforeUpdateUser(groups = {Update.class})
public class UserPo extends BasePo {
  @Serial private static final long serialVersionUID = 1L;

  /** 用户名 */
  @NotBlank(
      message = "用户名不能为空",
      groups = {Create.class, Update.class})
  @Size(
      min = 3,
      max = 30,
      message = "用户名长度应该为 3 ~ 30 个字符",
      groups = {Create.class, Update.class})
  private String username;

  /** 密码 */
  @NotBlank(
      message = "密码不能为空",
      groups = {Create.class})
  @Size(
      min = MIN_PASSWORD_LENGTH,
      max = MAX_PASSWORD_LENGTH,
      message = "密码长度应该为 " + MIN_PASSWORD_LENGTH + " ~ " + MAX_PASSWORD_LENGTH + " 个字符",
      groups = {Create.class})
  private String password;

  /** 真实姓名 */
  @Size(
      max = 30,
      message = "姓名长度不能大于 30 个字符",
      groups = {Create.class, Update.class})
  private String realName;

  /** 手机号 */
  @Size(
      max = 20,
      message = "手机号长度不能大于 20 个字符",
      groups = {Create.class, Update.class})
  private String phoneNumber;

  /** 用户性别：M-男，F-女，U-未知 */
  @Size(
      max = 1,
      message = "性别长度不能大于 1 个字符",
      groups = {Create.class, Update.class})
  private String gender;

  /** 头像路径 */
  @Size(
      max = 200,
      message = "头像路径长度不能大于 200 个字符",
      groups = {Create.class, Update.class})
  private String avatar;

  /** 是否启用 */
  private Boolean isEnabled;

  /** 禁用原因 */
  private String disabledReason;

  /** 最后登录IP */
  private String lastLoginIp;

  /** 最后登录时间 */
  private Date lastLoginDate;

  /** 备注 */
  private String remark;
}
