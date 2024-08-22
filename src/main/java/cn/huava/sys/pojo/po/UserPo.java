package cn.huava.sys.pojo.po;

import cn.huava.common.pojo.po.BasePo;
import com.baomidou.mybatisplus.annotation.TableName;
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
public class UserPo extends BasePo {
  @Serial private static final long serialVersionUID = 1L;

  /** 用户名 */
  private String username;

  /** 密码 */
  private String password;

  /** 真实姓名 */
  private String realName;

  /** 手机号 */
  private String phoneNumber;

  /** 用户性别：M-男，F-女，U-未知 */
  private String gender;

  /** 头像路径 */
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
