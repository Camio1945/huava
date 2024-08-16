package cn.huava.sys.service.jwt;

import cn.huava.sys.pojo.dto.SysUserJwtDto;
import java.util.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.json.jwt.JWTUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtAceService {
  private final JwtCreateTokenService jwtCreateTokenService;

  @Value("${project.jwt_key_base64}")
  private String jwtKeyBase64;

  public SysUserJwtDto createToken(@NonNull Long userId) {
    return jwtCreateTokenService.createToken(userId, getJwtKeyBytes());
  }

  private byte[] getJwtKeyBytes() {
    return Base64.getDecoder().decode(jwtKeyBase64);
  }

  public Long getUserIdFromAccessToken(@NonNull String token) {
    return JWTUtil.parseToken(token).getPayload("sub", Long.class);
  }

  public boolean verifyToken(@NonNull String token) {
    return JWTUtil.verify(token, getJwtKeyBytes());
  }
}
