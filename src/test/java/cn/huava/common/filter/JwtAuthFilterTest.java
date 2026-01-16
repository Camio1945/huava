package cn.huava.common.filter;

import static cn.huava.common.constant.CommonConstant.AUTHORIZATION_HEADER;
import static cn.huava.common.constant.CommonConstant.BEARER_PREFIX;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cn.huava.sys.cache.UserCache;
import cn.huava.sys.service.jwt.AceJwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for JwtAuthFilter
 *
 * @author Camio1945
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

  @Mock private AceJwtService jwtAceService;

  @Mock private UserCache userCache;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  @InjectMocks private JwtAuthFilter jwtAuthFilter;

  @BeforeEach
  void setUp() {
    // Reset mocks before each test
    reset(request, response, filterChain, jwtAceService, userCache);
  }

  @Test
  void should_return_unauthorized_when_expired_token() throws Exception {
    // Given
    String expiredToken = "expired.token.here";
    String authHeader = BEARER_PREFIX + expiredToken;

    when(request.getRequestURI()).thenReturn("/some/protected/resource");
    when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(authHeader);
    when(jwtAceService.isTokenExpired(expiredToken)).thenReturn(true);

    // Setup PrintWriter for response.getWriter() mock only for this test
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    when(response.getWriter()).thenReturn(printWriter);

    // When
    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    // Then
    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(response).getWriter(); // Ensure writer was accessed to write the response
    verify(filterChain, never()).doFilter(any(), any()); // Should not continue the filter chain
  }

  @Test
  void should_continue_filter_chain_when_valid_token() throws Exception {
    // Given
    String validToken = "valid.token.here";
    String authHeader = BEARER_PREFIX + validToken;

    when(request.getRequestURI()).thenReturn("/some/protected/resource");
    when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(authHeader);
    when(jwtAceService.isTokenExpired(validToken)).thenReturn(false);
    when(jwtAceService.getUserIdFromAccessToken(validToken)).thenReturn(1L);
    when(userCache.getById(1L)).thenReturn(new cn.huava.sys.pojo.po.UserExtPo());

    // When
    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    // Then
    verify(filterChain).doFilter(any(), any()); // Should continue the filter chain
    verify(response, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

  @Test
  void should_continue_filter_chain_when_no_token() throws Exception {
    // Given
    when(request.getRequestURI()).thenReturn("/some/protected/resource");
    when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(null);

    // When
    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    // Then
    verify(filterChain).doFilter(any(), any()); // Should continue the filter chain
    verify(response, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

  @Test
  void should_continue_filter_chain_when_token_without_bearer_prefix() throws Exception {
    // Given
    String tokenWithoutPrefix = "not_bearer_token"; // Has text but doesn't start with "Bearer "
    when(request.getRequestURI()).thenReturn("/some/protected/resource");
    when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(tokenWithoutPrefix);

    // When
    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    // Then
    verify(filterChain)
        .doFilter(
            any(),
            any()); // Should continue the filter chain since it doesn't start with Bearer prefix
    verify(response, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }

  @Test
  void should_skip_token_validation_when_refresh_token_uri() throws Exception {
    // Given
    when(request.getRequestURI()).thenReturn("/sys/user/refreshToken");

    // When
    jwtAuthFilter.doFilterInternal(request, response, filterChain);

    // Then
    verify(filterChain)
        .doFilter(any(), any()); // Should continue the filter chain without validation
    verify(jwtAceService, never()).isTokenExpired(any());
    verify(response, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  }
}
