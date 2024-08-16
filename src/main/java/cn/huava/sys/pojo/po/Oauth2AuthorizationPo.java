package cn.huava.sys.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import lombok.Data;

/**
 * @author Camio1945
 */
@Data
@TableName("oauth2_authorization")
public class Oauth2AuthorizationPo implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @TableId(value = "id", type = IdType.INPUT)
  private String id;

  private String registeredClientId;

  private String principalName;

  private String authorizationGrantType;

  private String authorizedScopes;

  private byte[] attributes;

  private String state;

  private byte[] authorizationCodeValue;

  private Timestamp authorizationCodeIssuedAt;

  private Timestamp authorizationCodeExpiresAt;

  private byte[] authorizationCodeMetadata;

  private byte[] accessTokenValue;

  private Timestamp accessTokenIssuedAt;

  private Timestamp accessTokenExpiresAt;

  private byte[] accessTokenMetadata;

  private String accessTokenType;

  private String accessTokenScopes;

  private byte[] oidcIdTokenValue;

  private Timestamp oidcIdTokenIssuedAt;

  private Timestamp oidcIdTokenExpiresAt;

  private byte[] oidcIdTokenMetadata;

  private byte[] refreshTokenValue;

  private Timestamp refreshTokenIssuedAt;

  private Timestamp refreshTokenExpiresAt;

  private byte[] refreshTokenMetadata;

  private byte[] userCodeValue;

  private Timestamp userCodeIssuedAt;

  private Timestamp userCodeExpiresAt;

  private byte[] userCodeMetadata;

  private byte[] deviceCodeValue;

  private Timestamp deviceCodeIssuedAt;

  private Timestamp deviceCodeExpiresAt;

  private byte[] deviceCodeMetadata;
}
