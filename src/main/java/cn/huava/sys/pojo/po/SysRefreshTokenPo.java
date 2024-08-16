package cn.huava.sys.pojo.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author Camio1945
 */
@Data
@TableName("sys_refresh_token")
public class SysRefreshTokenPo implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  @TableId private Long id;
  private String refreshToken;
  private Long sysUserId;
  private Date createTime;
  private Date updateTime;
  private Long deleteInfo;
}
