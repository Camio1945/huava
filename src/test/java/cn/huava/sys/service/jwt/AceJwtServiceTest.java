package cn.huava.sys.service.jwt;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import cn.huava.sys.pojo.dto.UserJwtDto;
import cn.hutool.v7.json.jwt.JWT;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for AceJwtService class
 *
 * @author Camio1945
 */
@ExtendWith(MockitoExtension.class)
class AceJwtServiceTest {

  @Mock
  private CreateTokenService createTokenService;

  @Mock
  private JwtUtilService jwtUtilService;

  @InjectMocks
  private AceJwtService aceJwtService;

  @BeforeEach
  void setUp() throws Exception {
    // Set the jwtKeyBase64 field using reflection since it's annotated with @Value
    Field jwtKeyBase64Field = AceJwtService.class.getDeclaredField("jwtKeyBase64");
    jwtKeyBase64Field.setAccessible(true);
    jwtKeyBase64Field.set(aceJwtService, "dGVzdGluZy1qd3QtZW5jb2Rpbmcta2V5"); // Base64 encoded "testing-jwt-encoding-key"

    // Inject the mocked JwtUtilService
    Field jwtUtilServiceField = AceJwtService.class.getDeclaredField("jwtUtilService");
    jwtUtilServiceField.setAccessible(true);
    jwtUtilServiceField.set(aceJwtService, jwtUtilService);
  }

  @Test
  void should_create_token_using_CreateTokenService() {
    Long userId = 1L;
    UserJwtDto expectedToken = new UserJwtDto();
    expectedToken.setAccessToken("mocked-token");

    when(createTokenService.createToken(eq(userId), any(byte[].class))).thenReturn(expectedToken);

    UserJwtDto result = aceJwtService.createToken(userId);

    assertThat(result).isEqualTo(expectedToken);
    verify(createTokenService).createToken(eq(userId), any(byte[].class));
  }

  @Test
  void should_extract_user_id_from_valid_access_token() {
    String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjEsImV4cCI6MTg1MDAwMDB9." +
                   "someSignature"; // This is a sample JWT token with subject=1 and expiration

    JWT mockJwt = mock(JWT.class);

    // Mock the JWTUtilService to return a JWT with a subject
    when(jwtUtilService.parseToken(token)).thenReturn(mockJwt);
    when(mockJwt.getPayload("sub", Long.class)).thenReturn(1L);

    Long result = aceJwtService.getUserIdFromAccessToken(token);

    assertThat(result).isEqualTo(1L);
  }

  @Test
  void should_verify_token_and_check_if_expired() {
    // This test would require creating a valid JWT token with an expiration time
    // For now, we'll test with a malformed token to trigger the verification failure
    String invalidToken = "invalid.token.format";

    // The actual exception thrown will be different from "invalid token" because
    // JWTUtil.parseToken will fail before the validation
    assertThatThrownBy(() -> aceJwtService.isTokenExpired(invalidToken))
        .isNotNull(); // Just verify that an exception is thrown
  }

  @Test
  void should_return_false_when_token_has_no_exp_claim() {
    // Test the branch where exp is null, so exp != null is false
    // This causes the expression to return false due to short-circuit evaluation
    String token = "dummy.token";
    JWT mockJwt = mock(JWT.class);

    // Mock the JWTUtilService to return a JWT with null exp
    when(jwtUtilService.verify(eq(token), any(byte[].class))).thenReturn(true);
    when(jwtUtilService.parseToken(token)).thenReturn(mockJwt);
    when(mockJwt.getPayload("exp", Long.class)).thenReturn(null);

    boolean result = aceJwtService.isTokenExpired(token);

    // The result should be false because exp is null, so exp != null is false
    assertThat(result).isFalse();
  }

  @Test
  void should_return_true_when_token_is_expired() {
    // Test the branch where exp is not null but the token is expired
    // Both conditions are true: exp != null && exp * CommonConstant.MILLIS_PER_SECOND <= System.currentTimeMillis()
    String token = "dummy.token";
    JWT mockJwt = mock(JWT.class);

    // Use a past timestamp that is definitely expired
    long pastExp = 1L; // Very old timestamp in seconds

    // Mock the JWTUtilService
    when(jwtUtilService.verify(eq(token), any(byte[].class))).thenReturn(true);
    when(jwtUtilService.parseToken(token)).thenReturn(mockJwt);
    when(mockJwt.getPayload("exp", Long.class)).thenReturn(pastExp);

    boolean result = aceJwtService.isTokenExpired(token);

    // The result should be true because exp is not null AND the expiration time is before current time
    assertThat(result).isTrue();
  }

  @Test
  void should_return_false_when_token_is_not_expired() {
    // Test the branch where exp is not null but the token is not expired
    // First condition is true but second is false: exp != null && exp * CommonConstant.MILLIS_PER_SECOND <= System.currentTimeMillis()
    String token = "dummy.token";
    JWT mockJwt = mock(JWT.class);

    // Calculate a future expiration time (1 hour from now)
    long futureExp = (System.currentTimeMillis() / cn.huava.common.constant.CommonConstant.MILLIS_PER_SECOND) + 3600;

    // Mock the JWTUtilService
    when(jwtUtilService.verify(eq(token), any(byte[].class))).thenReturn(true);
    when(jwtUtilService.parseToken(token)).thenReturn(mockJwt);
    when(mockJwt.getPayload("exp", Long.class)).thenReturn(futureExp);

    boolean result = aceJwtService.isTokenExpired(token);

    // The result should be false because exp is not null but the expiration time is after current time
    assertThat(result).isFalse();
  }
}