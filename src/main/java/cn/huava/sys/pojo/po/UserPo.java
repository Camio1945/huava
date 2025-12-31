package cn.huava.sys.pojo.po;

import static cn.huava.common.constant.CommonConstant.MAX_PASSWORD_LENGTH;
import static cn.huava.common.constant.CommonConstant.MIN_PASSWORD_LENGTH;

import cn.huava.common.pojo.po.BasePo;
import cn.huava.common.validation.Create;
import cn.huava.common.validation.Delete;
import cn.huava.common.validation.Update;
import cn.huava.common.validation.ValidEnum;
import cn.huava.sys.enumeration.UserGenderEnum;
import cn.huava.sys.validation.user.BeforeDeleteUser;
import cn.huava.sys.validation.user.BeforeUpdateUser;
import cn.huava.sys.validation.user.UniqueUsername;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.util.Date;
import lombok.Data;

/**
 * User
 *
 * @author Camio1945
 */
@Data
@TableName("sys_user")
@UniqueUsername(groups = {Create.class, Update.class})
@BeforeDeleteUser(groups = {Delete.class})
@BeforeUpdateUser(groups = {Update.class})
public class UserPo extends BasePo {
  @Serial private static final long serialVersionUID = 1L;

  /** Username */
  @NotBlank(
      message = "Username cannot be empty",
      groups = {Create.class, Update.class})
  @Size(
      min = 3,
      max = 30,
      message = "Username length should be 3 ~ 30 characters",
      groups = {Create.class, Update.class})
  private String username;

  /** Password */
  @NotBlank(
      message = "Password cannot be empty",
      groups = {Create.class})
  @Size(
      min = MIN_PASSWORD_LENGTH,
      max = MAX_PASSWORD_LENGTH,
      message =
          "Password length should be "
              + MIN_PASSWORD_LENGTH
              + " ~ "
              + MAX_PASSWORD_LENGTH
              + " characters",
      groups = {Create.class})
  private String password;

  /** Real name */
  @Size(
      max = 30,
      message = "Real name length cannot exceed 30 characters",
      groups = {Create.class, Update.class})
  private String realName;

  /** Phone number */
  @Size(
      max = 20,
      message = "Phone number length cannot exceed 20 characters",
      groups = {Create.class, Update.class})
  private String phoneNumber;

  /** User gender: M-Male, F-Female, U-Unknown */
  @Size(
      max = 1,
      message = "Gender length cannot exceed 1 character",
      groups = {Create.class, Update.class})
  @ValidEnum(
      enumClass = UserGenderEnum.class,
      message = "Gender options can only be: M, F, U",
      groups = {Create.class, Update.class})
  private String gender;

  /** Avatar path */
  @Size(
      max = 200,
      message = "Avatar path length cannot exceed 200 characters",
      groups = {Create.class, Update.class})
  private String avatar;

  /** Is enabled */
  private Boolean isEnabled;

  /** Disabled reason */
  private String disabledReason;

  /** Last login IP */
  private String lastLoginIp;

  /** Last login date */
  private Date lastLoginDate;

  /** Remark */
  private String remark;
}
