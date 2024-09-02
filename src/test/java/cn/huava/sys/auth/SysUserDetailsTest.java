package cn.huava.sys.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.huava.sys.pojo.po.UserPo;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;

class SysUserDetailsTest {

  private UserPo userPo;
  private Collection<? extends GrantedAuthority> authorities;
  private SysUserDetails sysUserDetails;

  @BeforeEach
  void setUp() {
    userPo = Mockito.mock(UserPo.class);
    authorities = Mockito.mock(Collection.class);
    sysUserDetails = new SysUserDetails(userPo, authorities);
  }

  @Test
  void testGetAuthorities() {
    assertEquals(authorities, sysUserDetails.getAuthorities());
  }

  @Test
  void testGetPassword() {
    String password = "password123";
    Mockito.when(userPo.getPassword()).thenReturn(password);
    assertEquals(password, sysUserDetails.getPassword());
  }

  @Test
  void testGetUsername() {
    String username = "user123";
    Mockito.when(userPo.getUsername()).thenReturn(username);
    assertEquals(username, sysUserDetails.getUsername());
  }

  @Test
  void testIsEnabled() {
    Mockito.when(userPo.getIsEnabled()).thenReturn(true);
    assertTrue(sysUserDetails.isEnabled());
  }
}
