package cn.huava.sys.service.user;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cn.huava.common.util.Fn;
import cn.huava.sys.pojo.dto.PermDto;
import cn.huava.sys.pojo.po.PermPo;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for {@link UserServiceUtil}
 *
 * @author Camio1945
 */
@ExtendWith(MockitoExtension.class)
class UserServiceUtilTest {

  private MockedStatic<Fn> fnMockedStatic;

  @BeforeEach
  void setUp() {
    fnMockedStatic = mockStatic(Fn.class);
  }

  @AfterEach
  void tearDown() {
    fnMockedStatic.close();
  }

  @Test
  void should_build_menu_tree_with_admin_role() {
    // Given
    PermPo perm1 = createPermPo(1L, 0L, "M", "Menu 1");
    PermPo perm2 = createPermPo(2L, 1L, "M", "Menu 2");
    PermPo perm3 = createPermPo(3L, 0L, "E", "Endpoint 3"); // Should be filtered out
    List<PermPo> perms = List.of(perm1, perm2, perm3);

    // Mock Fn.toBean to return PermDto instances
    PermDto dto1 = createPermDto(1L, 0L, "Menu 1");
    PermDto dto2 = createPermDto(2L, 1L, "Menu 2");
    fnMockedStatic
        .when(() -> Fn.toBean(any(PermPo.class), eq(PermDto.class)))
        .thenReturn(dto1, dto2);

    boolean isAdminRole = true;
    Set<Long> permIds = Set.of(); // Not used when admin role is true

    // When
    List<PermDto> result = UserServiceUtil.buildMenuTree(perms, isAdminRole, permIds);

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getName()).isEqualTo("Menu 1");
    // Note: Children won't be populated properly due to recursive nature and mocking limitations
    fnMockedStatic.verify(
        () -> Fn.toBean(any(PermPo.class), eq(PermDto.class)),
        times(2)); // Called for 2 valid perms
  }

  @Test
  void should_build_menu_tree_with_non_admin_role_and_perm_ids_contain_perms() {
    // Given
    PermPo perm1 = createPermPo(1L, 0L, "M", "Menu 1");
    PermPo perm2 = createPermPo(2L, 1L, "M", "Menu 2");
    PermPo perm3 = createPermPo(3L, 0L, "E", "Endpoint 3"); // Should be filtered out
    List<PermPo> perms = List.of(perm1, perm2, perm3);

    // Mock Fn.toBean to return PermDto instances
    PermDto dto1 = createPermDto(1L, 0L, "Menu 1");
    PermDto dto2 = createPermDto(2L, 1L, "Menu 2");
    fnMockedStatic
        .when(() -> Fn.toBean(any(PermPo.class), eq(PermDto.class)))
        .thenReturn(dto1, dto2);

    boolean isAdminRole = false;
    Set<Long> permIds = Set.of(1L, 2L); // Contains the perm IDs

    // When
    List<PermDto> result = UserServiceUtil.buildMenuTree(perms, isAdminRole, permIds);

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("Menu 1");
    fnMockedStatic.verify(
        () -> Fn.toBean(any(PermPo.class), eq(PermDto.class)),
        times(2)); // Called for 2 valid perms
  }

  @Test
  void should_build_menu_tree_with_non_admin_role_and_perm_ids_not_contain_perms() {
    // Given
    PermPo perm1 = createPermPo(1L, 0L, "M", "Menu 1");
    PermPo perm2 = createPermPo(2L, 1L, "M", "Menu 2");
    List<PermPo> perms = List.of(perm1, perm2);

    // Mock Fn.toBean to return PermDto instances
    fnMockedStatic
        .when(() -> Fn.toBean(any(PermPo.class), eq(PermDto.class)))
        .thenReturn(createPermDto(1L, 0L, "Menu 1"));

    boolean isAdminRole = false;
    Set<Long> permIds = Set.of(99L); // Does not contain the perm IDs

    // When
    List<PermDto> result = UserServiceUtil.buildMenuTree(perms, isAdminRole, permIds);

    // Then
    assertThat(result).isEmpty();
    fnMockedStatic.verify(
        () -> Fn.toBean(any(PermPo.class), eq(PermDto.class)),
        never()); // Not called because permIds don't match
  }

  @Test
  void should_build_menu_tree_with_empty_perms_list() {
    // Given
    List<PermPo> perms = List.of();
    boolean isAdminRole = false;
    Set<Long> permIds = Set.of();

    // When
    List<PermDto> result = UserServiceUtil.buildMenuTree(perms, isAdminRole, permIds);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void should_get_children_with_admin_role() {
    // Given
    long pid = 1L;
    PermPo perm2 = createPermPo(2L, 1L, "M", "Child Menu 1");
    PermPo perm3 = createPermPo(3L, 1L, "M", "Child Menu 2");
    PermPo perm4 = createPermPo(4L, 1L, "E", "Child Endpoint"); // Should be filtered out
    List<PermPo> perms = List.of(perm2, perm3, perm4);

    // Mock Fn.toBean to return PermDto instances
    PermDto dto2 = createPermDto(2L, 1L, "Child Menu 1");
    PermDto dto3 = createPermDto(3L, 1L, "Child Menu 2");
    fnMockedStatic
        .when(() -> Fn.toBean(any(PermPo.class), eq(PermDto.class)))
        .thenReturn(dto2, dto3);

    boolean isAdminRole = true;
    Set<Long> permIds = Set.of(); // Not used when admin role is true

    // When
    List<PermDto> result = UserServiceUtil.getChildren(pid, perms, isAdminRole, permIds);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getName()).isEqualTo("Child Menu 1");
    assertThat(result.get(1).getName()).isEqualTo("Child Menu 2");
    fnMockedStatic.verify(
        () -> Fn.toBean(any(PermPo.class), eq(PermDto.class)),
        times(2)); // Called for 2 valid perms
  }

  @Test
  void should_get_children_with_non_admin_role_and_perm_ids_contain_perms() {
    // Given
    long pid = 1L;
    PermPo perm2 = createPermPo(2L, 1L, "M", "Child Menu 1");
    PermPo perm3 = createPermPo(3L, 1L, "M", "Child Menu 2");
    List<PermPo> perms = List.of(perm2, perm3);

    // Mock Fn.toBean to return PermDto instances
    PermDto dto2 = createPermDto(2L, 1L, "Child Menu 1");
    PermDto dto3 = createPermDto(3L, 1L, "Child Menu 2");
    fnMockedStatic
        .when(() -> Fn.toBean(any(PermPo.class), eq(PermDto.class)))
        .thenReturn(dto2, dto3);

    boolean isAdminRole = false;
    Set<Long> permIds = Set.of(2L, 3L); // Contains the perm IDs

    // When
    List<PermDto> result = UserServiceUtil.getChildren(pid, perms, isAdminRole, permIds);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getName()).isEqualTo("Child Menu 1");
    assertThat(result.get(1).getName()).isEqualTo("Child Menu 2");
    fnMockedStatic.verify(
        () -> Fn.toBean(any(PermPo.class), eq(PermDto.class)),
        times(2)); // Called for 2 valid perms
  }

  @Test
  void should_get_children_with_non_admin_role_and_perm_ids_not_contain_perms() {
    // Given
    long pid = 1L;
    PermPo perm2 = createPermPo(2L, 1L, "M", "Child Menu 1");
    PermPo perm3 = createPermPo(3L, 1L, "M", "Child Menu 2");
    List<PermPo> perms = List.of(perm2, perm3);

    // Mock Fn.toBean to return PermDto instances
    fnMockedStatic
        .when(() -> Fn.toBean(any(PermPo.class), eq(PermDto.class)))
        .thenReturn(createPermDto(2L, 1L, "Child Menu 1"));

    boolean isAdminRole = false;
    Set<Long> permIds = Set.of(99L); // Does not contain the perm IDs

    // When
    List<PermDto> result = UserServiceUtil.getChildren(pid, perms, isAdminRole, permIds);

    // Then
    assertThat(result).isEmpty();
    fnMockedStatic.verify(
        () -> Fn.toBean(any(PermPo.class), eq(PermDto.class)),
        never()); // Not called because permIds don't match
  }

  @Test
  void should_get_children_with_empty_perms_list() {
    // Given
    long pid = 1L;
    List<PermPo> perms = List.of();
    boolean isAdminRole = false;
    Set<Long> permIds = Set.of();

    // When
    List<PermDto> result = UserServiceUtil.getChildren(pid, perms, isAdminRole, permIds);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  void should_get_children_recursively() {
    // Given
    long pid = 1L;
    PermPo perm2 = createPermPo(2L, 1L, "M", "Child Menu 1");
    PermPo perm3 = createPermPo(3L, 2L, "M", "Grandchild Menu 1"); // Child of Child Menu 1
    List<PermPo> perms = List.of(perm2, perm3);

    // Mock Fn.toBean to return PermDto instances
    PermDto dto2 = createPermDto(2L, 1L, "Child Menu 1");
    PermDto dto3 = createPermDto(3L, 2L, "Grandchild Menu 1");
    fnMockedStatic
        .when(() -> Fn.toBean(any(PermPo.class), eq(PermDto.class)))
        .thenReturn(dto2, dto3);

    boolean isAdminRole = true;
    Set<Long> permIds = Set.of();

    // When
    List<PermDto> result = UserServiceUtil.getChildren(pid, perms, isAdminRole, permIds);

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getName()).isEqualTo("Child Menu 1");
    fnMockedStatic.verify(
        () -> Fn.toBean(any(PermPo.class), eq(PermDto.class)),
        times(2)); // Called for 2 valid perms
  }

  @Test
  void should_filter_out_endpoint_type_perms_in_build_menu_tree() {
    // Given
    PermPo perm1 = createPermPo(1L, 0L, "M", "Menu 1");
    PermPo perm2 = createPermPo(2L, 0L, "E", "Endpoint 2"); // Should be filtered out
    List<PermPo> perms = List.of(perm1, perm2);

    // Mock Fn.toBean to return PermDto instances
    PermDto dto1 = createPermDto(1L, 0L, "Menu 1");
    fnMockedStatic.when(() -> Fn.toBean(any(PermPo.class), eq(PermDto.class))).thenReturn(dto1);

    boolean isAdminRole = true;
    Set<Long> permIds = Set.of();

    // When
    List<PermDto> result = UserServiceUtil.buildMenuTree(perms, isAdminRole, permIds);

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getName()).isEqualTo("Menu 1");
    fnMockedStatic.verify(
        () -> Fn.toBean(any(PermPo.class), eq(PermDto.class)),
        times(1)); // Called only for menu perm
  }

  @Test
  void should_filter_out_endpoint_type_perms_in_get_children() {
    // Given
    long pid = 0L;
    PermPo perm1 = createPermPo(1L, 0L, "M", "Menu 1");
    PermPo perm2 = createPermPo(2L, 0L, "E", "Endpoint 2"); // Should be filtered out
    List<PermPo> perms = List.of(perm1, perm2);

    // Mock Fn.toBean to return PermDto instances
    PermDto dto1 = createPermDto(1L, 0L, "Menu 1");
    fnMockedStatic.when(() -> Fn.toBean(any(PermPo.class), eq(PermDto.class))).thenReturn(dto1);

    boolean isAdminRole = true;
    Set<Long> permIds = Set.of();

    // When
    List<PermDto> result = UserServiceUtil.getChildren(pid, perms, isAdminRole, permIds);

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.getFirst().getName()).isEqualTo("Menu 1");
    fnMockedStatic.verify(
        () -> Fn.toBean(any(PermPo.class), eq(PermDto.class)),
        times(1)); // Called only for menu perm
  }

  private PermPo createPermPo(Long id, Long pid, String type, String name) {
    PermPo perm = mock(PermPo.class);
    lenient().when(perm.getId()).thenReturn(id);
    lenient().when(perm.getPid()).thenReturn(pid);
    lenient().when(perm.getType()).thenReturn(type);
    lenient().when(perm.getName()).thenReturn(name);
    return perm;
  }

  private PermDto createPermDto(Long id, Long pid, String name) {
    PermDto dto = new PermDto();
    dto.setId(id);
    dto.setPid(pid);
    dto.setName(name);
    return dto;
  }
}
