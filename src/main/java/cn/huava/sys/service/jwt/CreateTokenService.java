package cn.huava.sys.service.jwt;

import cn.huava.common.service.BaseService;
import cn.huava.sys.mapper.UserMapper;
import cn.huava.sys.pojo.dto.UserJwtDto;
import cn.huava.sys.pojo.po.UserPo;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.json.jwt.JWTUtil;
import org.springframework.stereotype.Service;

/**
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
class CreateTokenService extends BaseService<UserMapper, UserPo> {

  protected UserJwtDto createToken(@NonNull Long userId, byte[] jwtKey) {
    return new UserJwtDto()
        .setAccessToken(createAccessToken(userId, jwtKey))
        .setRefreshToken(createRefreshToken(jwtKey));
  }

  private static String createAccessToken(Long userId, byte[] jwtKey) {
    Map<String, Object> payload = HashMap.newHashMap(3);
    payload.put("sub", userId);
    payload.put("iat", System.currentTimeMillis() / 1000);
    payload.put("exp", System.currentTimeMillis() / 1000 + 60 * 60);
    return JWTUtil.createToken(payload, jwtKey);
  }

  private static String createRefreshToken(byte[] jwtKey) {
    Map<String, Object> payload = HashMap.newHashMap(3);
    payload.put("iat", System.currentTimeMillis() / 1000);
    payload.put("exp", System.currentTimeMillis() / 1000 + 60 * 60 * 24 * 30);
    return JWTUtil.createToken(payload, jwtKey);
  }
}
