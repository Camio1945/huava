package cn.huava.sys.pojo.po;

import static cn.huava.common.constant.CommonConstant.MAX_PASSWORD_LENGTH;
import static cn.huava.common.constant.CommonConstant.MIN_PASSWORD_LENGTH;

import cn.huava.common.pojo.po.BasePo;
import cn.huava.common.validation.*;
import cn.huava.sys.enumeration.UserGenderEnum;
import cn.huava.common.validation.ValidEnum;
import cn.huava.sys.validation.user.*;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.util.Date;
import java.util.Objects;
import lombok.Data;

/**
 * User
 *
 * @author Camio1945
 */
@TableName("sys_user")
@UniqueUsername(
    message = "Username already exists",
    groups = {Create.class, Update.class})
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
      message = "Password length should be " + MIN_PASSWORD_LENGTH + " ~ " + MAX_PASSWORD_LENGTH + " characters",
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

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRealName() {
    return realName;
  }

  public void setRealName(String realName) {
    this.realName = realName;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public Boolean getIsEnabled() {
    return isEnabled;
  }

  public void setIsEnabled(Boolean isEnabled) {
    this.isEnabled = isEnabled;
  }

  public String getDisabledReason() {
    return disabledReason;
  }

  public void setDisabledReason(String disabledReason) {
    this.disabledReason = disabledReason;
  }

  public String getLastLoginIp() {
    return lastLoginIp;
  }

  public void setLastLoginIp(String lastLoginIp) {
    this.lastLoginIp = lastLoginIp;
  }

  public Date getLastLoginDate() {
    return lastLoginDate;
  }

  public void setLastLoginDate(Date lastLoginDate) {
    this.lastLoginDate = lastLoginDate;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserPo userPo = (UserPo) o;
    return Objects.equals(getId(), userPo.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }
}