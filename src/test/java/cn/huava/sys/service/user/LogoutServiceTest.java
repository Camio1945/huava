package cn.huava.sys.service.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import cn.huava.sys.pojo.po.RefreshTokenPo;
import cn.huava.sys.service.refreshtoken.AceRefreshTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for {@link LogoutService}
 *
 * @author Camio1945
 */
@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

  @Mock private AceRefreshTokenService aceRefreshTokenService;

  @InjectMocks private LogoutService logoutService;

  @Test
  void should_logout_successfully_when_refresh_token_exists() {
    // Given
    String refreshToken = "valid_refresh_token";
    RefreshTokenPo refreshTokenPo = new RefreshTokenPo();
    refreshTokenPo.setId(1L);

    when(aceRefreshTokenService.getByRefreshToken(eq(refreshToken))).thenReturn(refreshTokenPo);

    // When
    logoutService.logout(refreshToken);

    // Then
    verify(aceRefreshTokenService).getByRefreshToken(eq(refreshToken));
    verify(aceRefreshTokenService).softDelete(eq(1L));
  }

  @Test
  void should_not_call_soft_delete_when_refresh_token_does_not_exist() {
    // Given
    String refreshToken = "invalid_refresh_token";

    when(aceRefreshTokenService.getByRefreshToken(eq(refreshToken))).thenReturn(null);

    // When
    logoutService.logout(refreshToken);

    // Then
    verify(aceRefreshTokenService).getByRefreshToken(eq(refreshToken));
    verify(aceRefreshTokenService, never()).softDelete(any(Long.class));
  }

  @Test
  void should_handle_null_refresh_token_gracefully() {
    // Given
    String refreshToken = null;

    // When
    logoutService.logout(refreshToken);

    // Then
    verify(aceRefreshTokenService).getByRefreshToken(eq(null));
    verify(aceRefreshTokenService, never()).softDelete(any(Long.class));
  }
}
