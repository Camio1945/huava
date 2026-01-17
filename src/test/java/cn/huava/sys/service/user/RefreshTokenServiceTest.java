package cn.huava.sys.service.user;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import cn.huava.common.constant.CommonConstant;
import cn.huava.sys.pojo.dto.UserJwtDto;
import cn.huava.sys.pojo.po.RefreshTokenPo;
import cn.huava.sys.service.jwt.AceJwtService;
import cn.huava.sys.service.refreshtoken.AceRefreshTokenService;
import cn.hutool.v7.json.jwt.JWT;
import cn.hutool.v7.json.jwt.JWTUtil;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for {@link RefreshTokenService}
 *
 * @author Camio1945
 */
@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

  @Mock private AceRefreshTokenService aceRefreshTokenService;
  @Mock private AceJwtService aceJwtService;

  private RefreshTokenService refreshTokenService;

  @BeforeEach
  void setUp() {
    refreshTokenService = new RefreshTokenService(aceRefreshTokenService, aceJwtService);
  }

  @Test
  void should_create_new_access_token_when_refresh_token_is_valid() {
    // Given
    String refreshToken = "valid_refresh_token";
    Long userId = 1L;
    String newAccessToken = "new_access_token";

    RefreshTokenPo refreshTokenPo = new RefreshTokenPo();
    refreshTokenPo.setSysUserId(userId);
    refreshTokenPo.setDeleteInfo(0L); // Not deleted

    JWT mockJwt = mock(JWT.class);
    when(mockJwt.getPayload("exp", Long.class))
        .thenReturn(Instant.now().plusSeconds(3600).getEpochSecond()); // Not expired

    UserJwtDto userJwtDto = new UserJwtDto();
    userJwtDto.setAccessToken(newAccessToken);

    // Mock static method JWTUtil.parseToken
    try (var mockedStatic = mockStatic(JWTUtil.class)) {
      mockedStatic.when(() -> JWTUtil.parseToken(refreshToken)).thenReturn(mockJwt);

      when(aceRefreshTokenService.getByRefreshToken(refreshToken)).thenReturn(refreshTokenPo);
      when(aceJwtService.createToken(userId)).thenReturn(userJwtDto);

      // When
      String result = refreshTokenService.refreshToken(refreshToken);

      // Then
      assertThat(result).isEqualTo(newAccessToken);
      verify(aceRefreshTokenService).getByRefreshToken(refreshToken);
      verify(aceJwtService).createToken(userId);
    }
  }

  @Test
  void should_throw_IllegalArgumentException_when_refresh_token_does_not_exist() {
    // Given
    String refreshToken = "invalid_refresh_token";

    when(aceRefreshTokenService.getByRefreshToken(refreshToken)).thenReturn(null);

    // When & Then
    assertThatThrownBy(() -> refreshTokenService.refreshToken(refreshToken))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Refresh token invalid");
    verify(aceRefreshTokenService).getByRefreshToken(refreshToken);
  }

  @Test
  void should_throw_IllegalArgumentException_when_refresh_token_is_deleted() {
    // Given
    String refreshToken = "deleted_refresh_token";

    RefreshTokenPo refreshTokenPo = new RefreshTokenPo();
    refreshTokenPo.setDeleteInfo(1L); // Deleted

    when(aceRefreshTokenService.getByRefreshToken(refreshToken)).thenReturn(refreshTokenPo);

    // When & Then
    assertThatThrownBy(() -> refreshTokenService.refreshToken(refreshToken))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Refresh token invalid");
    verify(aceRefreshTokenService).getByRefreshToken(refreshToken);
  }

  @Test
  void should_throw_IllegalArgumentException_when_refresh_token_is_expired() {
    // Given
    String refreshToken = "expired_refresh_token";

    RefreshTokenPo refreshTokenPo = new RefreshTokenPo();
    refreshTokenPo.setSysUserId(1L);
    refreshTokenPo.setDeleteInfo(0L); // Not deleted

    JWT mockJwt = mock(JWT.class);
    // Return an expiration time that is in the past (before current time)
    when(mockJwt.getPayload("exp", Long.class))
        .thenReturn((System.currentTimeMillis() - 1000) / CommonConstant.MILLIS_PER_SECOND);

    try (var mockedStatic = mockStatic(JWTUtil.class)) {
      mockedStatic.when(() -> JWTUtil.parseToken(refreshToken)).thenReturn(mockJwt);

      when(aceRefreshTokenService.getByRefreshToken(refreshToken)).thenReturn(refreshTokenPo);

      // When & Then
      assertThatThrownBy(() -> refreshTokenService.refreshToken(refreshToken))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Refresh token expired");
      verify(aceRefreshTokenService).getByRefreshToken(refreshToken);
    }
  }

  @Test
  void should_throw_IllegalArgumentException_when_refresh_token_has_null_exp_claim() {
    // Given
    String refreshToken = "refresh_token_with_null_exp";

    RefreshTokenPo refreshTokenPo = new RefreshTokenPo();
    refreshTokenPo.setSysUserId(1L);
    refreshTokenPo.setDeleteInfo(0L); // Not deleted

    JWT mockJwt = mock(JWT.class);
    when(mockJwt.getPayload("exp", Long.class)).thenReturn(null); // Null expiration

    try (var mockedStatic = mockStatic(JWTUtil.class)) {
      mockedStatic.when(() -> JWTUtil.parseToken(refreshToken)).thenReturn(mockJwt);

      when(aceRefreshTokenService.getByRefreshToken(refreshToken)).thenReturn(refreshTokenPo);

      // When & Then
      assertThatThrownBy(() -> refreshTokenService.refreshToken(refreshToken))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("Refresh token expired");
      verify(aceRefreshTokenService).getByRefreshToken(refreshToken);
    }
  }
}
