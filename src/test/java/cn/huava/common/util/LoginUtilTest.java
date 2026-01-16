package cn.huava.common.util;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import cn.huava.sys.cache.UserCache;
import cn.huava.sys.pojo.po.UserExtPo;
import cn.huava.sys.pojo.po.UserPo;
import java.lang.reflect.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Tests for LoginUtil class
 *
 * @author Camio1945
 */
@ExtendWith(MockitoExtension.class)
class LoginUtilTest {

  @Mock private UserCache userCache;

  @BeforeEach
  void setUp() {
    // Setup any common  initialization here if needed
  }

  @Test
  void should_return_null_when_authentication_is_null() throws Exception {
    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
        mockStatic(SecurityContextHolder.class)) {

      SecurityContext mockSecurityContext = mock(SecurityContext.class);
      when(mockSecurityContext.getAuthentication()).thenReturn(null);
      mockedSecurityContextHolder
          .when(SecurityContextHolder::getContext)
          .thenReturn(mockSecurityContext);

      try (MockedStatic<Fn> mockedFn = mockStatic(Fn.class)) {
        mockedFn.when(() -> Fn.getBean(UserCache.class)).thenReturn(userCache);

        // Access the private method using reflection
        Method getLoginUserMethod = LoginUtil.class.getDeclaredMethod("getLoginUser");
        getLoginUserMethod.setAccessible(true);
        UserPo result = (UserPo) getLoginUserMethod.invoke(null);

        assertThat(result).isNull();
      }
    }
  }

  @Test
  void should_return_null_when_principal_is_null() throws Exception {
    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
        mockStatic(SecurityContextHolder.class)) {

      UsernamePasswordAuthenticationToken mockAuthentication =
          mock(UsernamePasswordAuthenticationToken.class);
      when(mockAuthentication.getPrincipal()).thenReturn(null);

      SecurityContext mockSecurityContext = mock(SecurityContext.class);
      when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
      mockedSecurityContextHolder
          .when(SecurityContextHolder::getContext)
          .thenReturn(mockSecurityContext);

      try (MockedStatic<Fn> mockedFn = mockStatic(Fn.class)) {
        mockedFn.when(() -> Fn.getBean(UserCache.class)).thenReturn(userCache);

        // Access the private method using reflection
        Method getLoginUserMethod = LoginUtil.class.getDeclaredMethod("getLoginUser");
        getLoginUserMethod.setAccessible(true);
        UserPo result = (UserPo) getLoginUserMethod.invoke(null);

        assertThat(result).isNull();
      }
    }
  }

  @Test
  void should_return_null_when_user_not_found_in_cache() throws Exception {
    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
        mockStatic(SecurityContextHolder.class)) {

      UserDetails mockUserDetails = mock(UserDetails.class);
      when(mockUserDetails.getUsername()).thenReturn("user");

      UsernamePasswordAuthenticationToken mockAuthentication =
          mock(UsernamePasswordAuthenticationToken.class);
      when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);

      SecurityContext mockSecurityContext = mock(SecurityContext.class);
      when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
      mockedSecurityContextHolder
          .when(SecurityContextHolder::getContext)
          .thenReturn(mockSecurityContext);

      try (MockedStatic<Fn> mockedFn = mockStatic(Fn.class)) {
        mockedFn.when(() -> Fn.getBean(UserCache.class)).thenReturn(userCache);
        when(userCache.getIdByUsername("user")).thenReturn(null);

        // Access the private method using reflection
        Method getLoginUserMethod = LoginUtil.class.getDeclaredMethod("getLoginUser");
        getLoginUserMethod.setAccessible(true);
        UserPo result = (UserPo) getLoginUserMethod.invoke(null);

        assertThat(result).isNull();
      }
    }
  }

  @Test
  void should_return_null_when_getById_returns_null() throws Exception {
    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
        mockStatic(SecurityContextHolder.class)) {

      UserDetails mockUserDetails = mock(UserDetails.class);
      when(mockUserDetails.getUsername()).thenReturn("user");

      UsernamePasswordAuthenticationToken mockAuthentication =
          mock(UsernamePasswordAuthenticationToken.class);
      when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);

      SecurityContext mockSecurityContext = mock(SecurityContext.class);
      when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
      mockedSecurityContextHolder
          .when(SecurityContextHolder::getContext)
          .thenReturn(mockSecurityContext);

      try (MockedStatic<Fn> mockedFn = mockStatic(Fn.class)) {
        mockedFn.when(() -> Fn.getBean(UserCache.class)).thenReturn(userCache);
        when(userCache.getIdByUsername("user")).thenReturn(1L);
        when(userCache.getById(1L)).thenReturn(null);

        // Access the private method using reflection
        Method getLoginUserMethod = LoginUtil.class.getDeclaredMethod("getLoginUser");
        getLoginUserMethod.setAccessible(true);
        UserPo result = (UserPo) getLoginUserMethod.invoke(null);

        assertThat(result).isNull();
      }
    }
  }

  @Test
  void should_return_user_when_user_exists_in_cache() throws Exception {
    UserExtPo expectedUser = new UserExtPo();

    try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder =
        mockStatic(SecurityContextHolder.class)) {

      UserDetails mockUserDetails = mock(UserDetails.class);
      when(mockUserDetails.getUsername()).thenReturn("user");

      UsernamePasswordAuthenticationToken mockAuthentication =
          mock(UsernamePasswordAuthenticationToken.class);
      when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);

      SecurityContext mockSecurityContext = mock(SecurityContext.class);
      when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
      mockedSecurityContextHolder
          .when(SecurityContextHolder::getContext)
          .thenReturn(mockSecurityContext);

      try (MockedStatic<Fn> mockedFn = mockStatic(Fn.class)) {
        mockedFn.when(() -> Fn.getBean(UserCache.class)).thenReturn(userCache);
        when(userCache.getIdByUsername("user")).thenReturn(1L);
        when(userCache.getById(1L)).thenReturn(expectedUser);

        // Access the private method using reflection
        Method getLoginUserMethod = LoginUtil.class.getDeclaredMethod("getLoginUser");
        getLoginUserMethod.setAccessible(true);
        UserPo result = (UserPo) getLoginUserMethod.invoke(null);

        assertThat(result).isEqualTo(expectedUser);
      }
    }
  }
}
