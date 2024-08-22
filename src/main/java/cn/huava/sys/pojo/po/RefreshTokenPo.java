package cn.huava.sys.pojo.po;

import cn.huava.common.pojo.po.BasePo;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Camio1945
 */
@Data
@TableName("sys_refresh_token")
public class RefreshTokenPo extends BasePo {
  private String refreshToken;
  private Long sysUserId;
}
