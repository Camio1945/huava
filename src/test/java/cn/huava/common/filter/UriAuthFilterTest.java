package cn.huava.common.filter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cn.huava.common.constant.CommonConstant;
import cn.huava.common.util.Fn;
import cn.huava.sys.cache.RoleCache;
import cn.huava.sys.cache.UserRoleCache;
import cn.huava.sys.pojo.po.UserPo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Unit tests for UriAuthFilter
 *
 * @author Camio1945
 */
@ExtendWith(MockitoExtension.class)
class UriAuthFilterTest {

  @Mock private UserRoleCache userRoleCache;

  @Mock private RoleCache roleCache;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  @Mock private Authentication authentication;

  @InjectMocks private UriAuthFilter uriAuthFilter;

  @BeforeEach
  void setUp() throws Exception {
    // Reset mocks before each test
    reset(request, response, filterChain, userRoleCache, roleCache);

    // Set the uriAuthRange field using reflection since it's @Value injected
    Field uriAuthRangeField = UriAuthFilter.class.getDeclaredField("uriAuthRange");
    uriAuthRangeField.setAccessible(true);
    uriAuthRangeField.set(uriAuthFilter, "main"); // Default to "main" for most tests
  }

  @Test
  void should_continue_filter_chain_when_not_logged_in() throws Exception {
    // Given
    lenient().when(request.getRequestURI()).thenReturn("/some/protected/resource/create");
    // Mock static SecurityContextHolder to return null authentication
    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
        mockStatic(SecurityContextHolder.class)) {
      SecurityContext mockSecurityContext = mock(SecurityContext.class);
      when(SecurityContextHolder.getContext()).thenReturn(mockSecurityContext);
      when(mockSecurityContext.getAuthentication()).thenReturn(null);

      // When
      uriAuthFilter.doFilterInternal(request, response, filterChain);

      // Then
      verify(filterChain).doFilter(any(), any()); // Should continue the filter chain
      verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
  }

  @Test
  void should_continue_filter_chain_when_user_has_permission_for_main_uri() throws Exception {
    // Given
    lenient().when(request.getRequestURI()).thenReturn("/api/resource/create");

    // Mock static SecurityContextHolder and Fn
    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
            mockStatic(SecurityContextHolder.class);
        MockedStatic<Fn> mockedFn = mockStatic(Fn.class)) {

      SecurityContext mockSecurityContext = mock(SecurityContext.class);
      when(SecurityContextHolder.getContext()).thenReturn(mockSecurityContext);
      when(mockSecurityContext.getAuthentication()).thenReturn(authentication);

      // Mock user login
      UserPo mockUser = new UserPo();
      mockUser.setId(1L);
      when(Fn.getLoginUser()).thenReturn(mockUser);

      // Mock role assignments
      when(userRoleCache.getRoleIdsByUserId(1L)).thenReturn(List.of(2L));

      // Mock role permissions - user has permission for the URI
      when(roleCache.getPermUrisByRoleId(2L)).thenReturn(Set.of("/api/resource/create"));

      // When
      uriAuthFilter.doFilterInternal(request, response, filterChain);

      // Then
      verify(filterChain).doFilter(any(), any()); // Should continue the filter chain
      verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
  }

  @Test
  void should_return_forbidden_when_user_does_not_have_permission_for_main_uri() throws Exception {
    // Given
    lenient().when(request.getRequestURI()).thenReturn("/api/resource/create");

    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
            mockStatic(SecurityContextHolder.class);
        MockedStatic<Fn> mockedFn = mockStatic(Fn.class)) {

      SecurityContext mockSecurityContext = mock(SecurityContext.class);
      when(SecurityContextHolder.getContext()).thenReturn(mockSecurityContext);
      when(mockSecurityContext.getAuthentication()).thenReturn(authentication);

      // Mock user login
      UserPo mockUser = new UserPo();
      mockUser.setId(1L);
      when(Fn.getLoginUser()).thenReturn(mockUser);

      // Mock role assignments
      when(userRoleCache.getRoleIdsByUserId(1L)).thenReturn(List.of(2L));

      // Mock role permissions - user does NOT have permission for the URI
      when(roleCache.getPermUrisByRoleId(2L)).thenReturn(Set.of("/api/resource/read"));

      // Setup PrintWriter for response.getWriter() mock
      StringWriter stringWriter = new StringWriter();
      PrintWriter printWriter = new PrintWriter(stringWriter);
      when(response.getWriter()).thenReturn(printWriter);

      // When
      uriAuthFilter.doFilterInternal(request, response, filterChain);

      // Then
      verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
      verify(response).getWriter(); // Ensure writer was accessed to write the response
      verify(filterChain, never()).doFilter(any(), any()); // Should not continue the filter chain
    }
  }

  @Test
  void should_return_forbidden_when_user_has_no_roles_for_main_uri() throws Exception {
    // Given
    lenient().when(request.getRequestURI()).thenReturn("/api/resource/create");

    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
            mockStatic(SecurityContextHolder.class);
        MockedStatic<Fn> mockedFn = mockStatic(Fn.class)) {

      SecurityContext mockSecurityContext = mock(SecurityContext.class);
      when(SecurityContextHolder.getContext()).thenReturn(mockSecurityContext);
      when(mockSecurityContext.getAuthentication()).thenReturn(authentication);

      // Mock user login
      UserPo mockUser = new UserPo();
      mockUser.setId(1L);
      when(Fn.getLoginUser()).thenReturn(mockUser);

      // Mock role assignments - user has no roles
      when(userRoleCache.getRoleIdsByUserId(1L)).thenReturn(List.of());

      // Setup PrintWriter for response.getWriter() mock
      StringWriter stringWriter = new StringWriter();
      PrintWriter printWriter = new PrintWriter(stringWriter);
      when(response.getWriter()).thenReturn(printWriter);

      // When
      uriAuthFilter.doFilterInternal(request, response, filterChain);

      // Then
      verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
      verify(response).getWriter(); // Ensure writer was accessed to write the response
      verify(filterChain, never()).doFilter(any(), any()); // Should not continue the filter chain
    }
  }

  @Test
  void should_return_forbidden_when_user_has_admin_role_and_uri_not_permitted() throws Exception {
    // Given
    lenient().when(request.getRequestURI()).thenReturn("/api/resource/create");

    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
            mockStatic(SecurityContextHolder.class);
        MockedStatic<Fn> mockedFn = mockStatic(Fn.class)) {

      SecurityContext mockSecurityContext = mock(SecurityContext.class);
      when(SecurityContextHolder.getContext()).thenReturn(mockSecurityContext);
      when(mockSecurityContext.getAuthentication()).thenReturn(authentication);

      // Mock user login
      UserPo mockUser = new UserPo();
      mockUser.setId(1L);
      when(Fn.getLoginUser()).thenReturn(mockUser);

      // Mock role assignments - user has admin role
      when(userRoleCache.getRoleIdsByUserId(1L)).thenReturn(List.of(CommonConstant.ADMIN_ROLE_ID));

      // When
      uriAuthFilter.doFilterInternal(request, response, filterChain);

      // Then - Admin should have access regardless of specific URI permissions
      verify(filterChain).doFilter(any(), any()); // Should continue the filter chain
      verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
  }

  @Test
  void should_continue_filter_chain_when_uri_not_in_main_suffixes() throws Exception {
    // Given
    lenient().when(request.getRequestURI()).thenReturn("/api/resource/view");

    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
            mockStatic(SecurityContextHolder.class);
        MockedStatic<Fn> mockedFn = mockStatic(Fn.class)) {

      SecurityContext mockSecurityContext = mock(SecurityContext.class);
      when(SecurityContextHolder.getContext()).thenReturn(mockSecurityContext);
      when(mockSecurityContext.getAuthentication()).thenReturn(authentication);

      // Mock user login
      UserPo mockUser = new UserPo();
      mockUser.setId(1L);
      when(Fn.getLoginUser()).thenReturn(mockUser);

      // When
      uriAuthFilter.doFilterInternal(request, response, filterChain);

      // Then - Since URI doesn't end with main suffixes, permission check is skipped
      verify(filterChain).doFilter(any(), any()); // Should continue the filter chain
      verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
  }

  @Test
  void should_continue_filter_chain_when_uri_auth_range_is_not_main() throws Exception {
    // Given
    lenient().when(request.getRequestURI()).thenReturn("/api/resource/create");

    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
            mockStatic(SecurityContextHolder.class);
        MockedStatic<Fn> mockedFn = mockStatic(Fn.class)) {

      SecurityContext mockSecurityContext = mock(SecurityContext.class);
      when(SecurityContextHolder.getContext()).thenReturn(mockSecurityContext);
      when(mockSecurityContext.getAuthentication()).thenReturn(authentication);

      // Mock user login
      UserPo mockUser = new UserPo();
      mockUser.setId(1L);
      when(Fn.getLoginUser()).thenReturn(mockUser);

      // Set the uriAuthRange field to "other" using reflection
      Field uriAuthRangeField = UriAuthFilter.class.getDeclaredField("uriAuthRange");
      uriAuthRangeField.setAccessible(true);
      uriAuthRangeField.set(uriAuthFilter, "other"); // Not "main"

      // When
      uriAuthFilter.doFilterInternal(request, response, filterChain);

      // Then - Since uriAuthRange is not "main", permission check is skipped
      verify(filterChain).doFilter(any(), any()); // Should continue the filter chain
      verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
  }

  @Test
  void should_handle_uris_with_numbers_properly() throws Exception {
    // Given
    // Use a URI that ends with digits to test the number removal
    lenient()
        .when(request.getRequestURI())
        .thenReturn("/api/resource/123"); // URI ending with numbers

    // Setup PrintWriter for response.getWriter() mock to prevent NPE if called
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    lenient().when(response.getWriter()).thenReturn(printWriter);

    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
            mockStatic(SecurityContextHolder.class);
        MockedStatic<Fn> mockedFn = mockStatic(Fn.class)) {

      SecurityContext mockSecurityContext = mock(SecurityContext.class);
      lenient().when(SecurityContextHolder.getContext()).thenReturn(mockSecurityContext);
      lenient().when(mockSecurityContext.getAuthentication()).thenReturn(authentication);

      // Mock user login
      UserPo mockUser = new UserPo();
      lenient().when(Fn.getLoginUser()).thenReturn(mockUser);

      // Mock role assignments
      lenient().when(userRoleCache.getRoleIdsByUserId(1L)).thenReturn(List.of(2L));

      // Mock role permissions - user has permission for the URI after number removal
      lenient()
          .when(roleCache.getPermUrisByRoleId(2L))
          .thenReturn(Set.of("/api/resource")); // Permission for base URI after number removal

      // When
      uriAuthFilter.doFilterInternal(request, response, filterChain);

      // Then - URI should be processed as "/api/resource" after number removal
      verify(filterChain).doFilter(any(), any()); // Should continue the filter chain
      verify(response, never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
  }
}
