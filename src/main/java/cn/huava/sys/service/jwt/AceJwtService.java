package cn.huava.sys.service.jwt;

import cn.huava.common.constant.CommonConstant;
import cn.huava.sys.pojo.dto.UserJwtDto;
import cn.hutool.v7.core.lang.Assert;
import cn.hutool.v7.json.jwt.JWT;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * JWT 服务主入口类<br>
 *
 * @author Camio1945
 */
@Slf4j
@NullMarked
@Service
@RequiredArgsConstructor
public class AceJwtService {
  private final CreateTokenService createTokenService;

  private final JwtUtilService jwtUtilService = new JwtUtilService();

  @Value("${project.jwt_key_base64}")
  private String jwtKeyBase64;

  public UserJwtDto createToken(Long userId) {
    return createTokenService.createToken(userId, getJwtKeyBytes());
  }

  private byte[] getJwtKeyBytes() {
    return Base64.getDecoder().decode(jwtKeyBase64);
  }

  public Long getUserIdFromAccessToken(final String token) {
    return jwtUtilService.parseToken(token).getPayload("sub", Long.class);
  }

  public boolean isTokenExpired(final String token) {
    Assert.isTrue(jwtUtilService.verify(token, getJwtKeyBytes()), "invalid token");
    JWT jwt = jwtUtilService.parseToken(token);
    Long exp = jwt.getPayload("exp", Long.class);
    return exp != null && exp * CommonConstant.MILLIS_PER_SECOND <= System.currentTimeMillis();
  }
}
