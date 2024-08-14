package cn.huava.sys.pojo.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.Instant;
import lombok.Data;

/**
 * @author Camio1945
 */
@Data
@TableName("oauth2_refresh_tokens")
public class Oauth2RefreshToken {

  @TableId private Long id;

  private String token;

  private String authenticationId;

  private String userName;

  private String clientId;

  private Instant expiryDate;
}
