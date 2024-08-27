package cn.huava.sys.pojo.qo;

import static cn.huava.common.constant.CommonConstant.MAX_PASSWORD_LENGTH;
import static cn.huava.common.constant.CommonConstant.MIN_PASSWORD_LENGTH;

import cn.huava.common.validation.Create;
import cn.huava.sys.validation.user.Password;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author Camio1945
 */
@Data
@Password
public class UpdatePasswordQo {
  @NotBlank(message = "旧密码不能为空")
  private String oldPassword;

  @NotBlank(message = "新密码不能为空")
  @Size(
      min = MIN_PASSWORD_LENGTH,
      max = MAX_PASSWORD_LENGTH,
      message = "新密码长度应该为 " + MIN_PASSWORD_LENGTH + " ~ " + MAX_PASSWORD_LENGTH + " 个字符",
      groups = {Create.class})
  private String newPassword;
}
