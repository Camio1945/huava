package cn.huava.sys.service.role;

import static cn.huava.common.constant.CommonConstant.ADMIN_ROLE_ID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

import cn.huava.common.pojo.dto.PageDto;
import cn.huava.common.pojo.qo.PageQo;
import cn.huava.sys.cache.RoleCache;
import cn.huava.sys.mapper.RoleMapper;
import cn.huava.sys.pojo.po.RolePermPo;
import cn.huava.sys.pojo.po.RolePo;
import cn.huava.sys.pojo.qo.SetPermQo;
import cn.huava.sys.service.roleperm.AceRolePermService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for {@link AceRoleService}
 *
 * @author Camio1945
 */
@ExtendWith(MockitoExtension.class)
class AceRoleServiceTest {

  @Mock private RolePageService rolePageService;
  @Mock private AceRolePermService rolePermService;
  @Mock private RoleCache roleCache;
  @Mock private RoleMapper roleMapper;

  private AceRoleService aceRoleService;

  @BeforeEach
  void setUp() {
    aceRoleService = spy(new AceRoleService(rolePageService, rolePermService, roleCache));
    // Mock the mapper for the BaseService methods with lenient stubbing to avoid unnecessary
    // stubbing errors
    lenient().doReturn(roleMapper).when(aceRoleService).getBaseMapper();
  }

  @Test
  void should_return_role_page_from_rolePageService() {
    // Given
    PageQo<RolePo> pageQo = new PageQo<>();
    RolePo params = new RolePo();
    PageDto<RolePo> expectedPage = new PageDto<>();
    when(rolePageService.rolePage(pageQo, params)).thenReturn(expectedPage);

    // When
    PageDto<RolePo> actualPage = aceRoleService.rolePage(pageQo, params);

    // Then
    assertThat(actualPage).isEqualTo(expectedPage);
    verify(rolePageService).rolePage(pageQo, params);
  }

  @Test
  void should_check_name_exists_with_id_provided() {
    // Given
    Long id = 1L;
    String name = "admin";
    when(aceRoleService.exists(any(LambdaQueryWrapper.class))).thenReturn(true);

    // When
    boolean result = aceRoleService.isNameExists(id, name);

    // Then
    assertThat(result).isTrue();
    verify(aceRoleService).exists(any(LambdaQueryWrapper.class));
  }

  @Test
  void should_check_name_exists_with_null_id() {
    // Given
    Long id = null;
    String name = "admin";
    when(aceRoleService.exists(any(LambdaQueryWrapper.class))).thenReturn(false);

    // When
    boolean result = aceRoleService.isNameExists(id, name);

    // Then
    assertThat(result).isFalse();
    verify(aceRoleService).exists(any(LambdaQueryWrapper.class));
  }

  @Test
  void should_throw_exception_when_setting_permission_for_admin_role() {
    // Given
    SetPermQo setPermQo = new SetPermQo();
    setPermQo.setRoleId(ADMIN_ROLE_ID); // ADMIN_ROLE_ID constant

    // When & Then
    assertThatThrownBy(() -> aceRoleService.setPerm(setPermQo))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("不允许修改超级管理员角色的权限");
  }

  @Test
  void should_set_permissions_successfully_with_empty_perm_ids() {
    // Given
    SetPermQo setPermQo = new SetPermQo();
    setPermQo.setRoleId(2L);
    setPermQo.setPermIds(List.of()); // Empty list

    // When
    aceRoleService.setPerm(setPermQo);

    // Then
    verify(rolePermService).remove(any(LambdaQueryWrapper.class));
    verify(rolePermService, never()).saveBatch(any());
    verify(roleCache).deleteCache(2L);
  }

  @Test
  void should_set_permissions_successfully_with_non_empty_perm_ids() {
    // Given
    SetPermQo setPermQo = new SetPermQo();
    setPermQo.setRoleId(2L);
    setPermQo.setPermIds(List.of(10L, 20L, 30L));

    // When
    aceRoleService.setPerm(setPermQo);

    // Then
    verify(rolePermService).remove(any(LambdaQueryWrapper.class));
    verify(rolePermService).saveBatch(any());
    verify(roleCache).deleteCache(2L);
  }

  @Test
  void should_get_permissions_for_role() {
    // Given
    Long roleId = 1L;
    List<RolePermPo> rolePermPos = List.of(new RolePermPo(1L, 10L), new RolePermPo(1L, 20L));
    when(rolePermService.list(any(LambdaQueryWrapper.class))).thenReturn(rolePermPos);

    // When
    List<Long> permIds = aceRoleService.getPerm(roleId);

    // Then
    assertThat(permIds).containsExactly(10L, 20L);
    verify(rolePermService).list(any(LambdaQueryWrapper.class));
  }

  @Test
  void should_return_empty_list_when_no_permissions_found() {
    // Given
    Long roleId = 1L;
    when(rolePermService.list(any(LambdaQueryWrapper.class))).thenReturn(List.of());

    // When
    List<Long> permIds = aceRoleService.getPerm(roleId);

    // Then
    assertThat(permIds).isEmpty();
    verify(rolePermService).list(any(LambdaQueryWrapper.class));
  }
}
