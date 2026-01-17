package cn.huava.sys.service.user;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import cn.huava.common.pojo.dto.PageDto;
import cn.huava.common.pojo.qo.PageQo;
import cn.huava.sys.mapper.UserMapper;
import cn.huava.sys.pojo.dto.UserDto;
import cn.huava.sys.pojo.po.UserExtPo;
import cn.huava.sys.pojo.po.UserPo;
import cn.huava.sys.pojo.po.UserRolePo;
import cn.huava.sys.service.userrole.AceUserRoleService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import java.util.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests for {@link UserPageService}
 *
 * @author Camio1945
 */
@ExtendWith(MockitoExtension.class)
class UserPageServiceTest {

  @Mock private AceUserRoleService userRoleService;

  @Mock private UserMapper userMapper;

  private UserPageService userPageService;

  // Inner class to expose protected methods for testing
  static class TestableUserPageService extends UserPageService {
    public TestableUserPageService(AceUserRoleService userRoleService) {
      super(userRoleService);
    }

    public PageDto<UserDto> callUserPage(PageQo<UserExtPo> pageQo, UserExtPo params) {
      return userPage(pageQo, params);
    }
  }

  @BeforeEach
  void setUp() {
    userPageService = new UserPageService(userRoleService);
  }

  @Test
  void should_create_instance_successfully() {
    Assertions.assertThat(userPageService).isNotNull();
  }

  @Test
  void should_return_page_dto_with_user_dtos_and_count_when_user_page_is_called() {
    // Arrange
    TestableUserPageService testableService = spy(new TestableUserPageService(userRoleService));

    PageQo<UserExtPo> pageQo = new PageQo<>();
    List<UserExtPo> records = Arrays.asList(
        createUserExtPo(1L, "john_doe", "John Doe", "1234567890"),
        createUserExtPo(2L, "jane_smith", "Jane Smith", "0987654321")
    );
    pageQo.setRecords(records);
    pageQo.setTotal(2L);

    UserExtPo params = new UserExtPo();

    // Mock the page method to return the same pageQo
    doReturn(pageQo).when(testableService).page(any(), any());

    // Mock the role service to return empty roles
    when(userRoleService.list(any(LambdaUpdateWrapper.class))).thenReturn(Collections.emptyList());

    // Act
    PageDto<UserDto> result = testableService.callUserPage(pageQo, params);

    // Assert
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getList()).hasSize(2);
    Assertions.assertThat(result.getCount()).isEqualTo(2L);

    UserDto firstUser = result.getList().get(0);
    Assertions.assertThat(firstUser.getId()).isEqualTo(1L);
    Assertions.assertThat(firstUser.getUsername()).isEqualTo("john_doe");
    Assertions.assertThat(firstUser.getRealName()).isEqualTo("John Doe");
    Assertions.assertThat(firstUser.getPhoneNumber()).isEqualTo("1234567890");

    UserDto secondUser = result.getList().get(1);
    Assertions.assertThat(secondUser.getId()).isEqualTo(2L);
    Assertions.assertThat(secondUser.getUsername()).isEqualTo("jane_smith");
    Assertions.assertThat(secondUser.getRealName()).isEqualTo("Jane Smith");
    Assertions.assertThat(secondUser.getPhoneNumber()).isEqualTo("0987654321");

    verify(userRoleService).list(any(LambdaUpdateWrapper.class));
  }

  private UserExtPo createUserExtPo(Long id, String username, String realName, String phoneNumber) {
    UserExtPo user = new UserExtPo();
    user.setId(id);
    user.setUsername(username);
    user.setRealName(realName);
    user.setPhoneNumber(phoneNumber);
    return user;
  }

  @Test
  void should_map_user_ids_to_role_ids_correctly_when_get_user_id_to_role_ids_map_is_called() {
    // Arrange
    List<UserRolePo> userRoles = Arrays.asList(
        createUserRolePo(1L, 10L),
        createUserRolePo(1L, 11L),
        createUserRolePo(2L, 12L),
        createUserRolePo(3L, 13L),
        createUserRolePo(3L, 14L)
    );

    // Act - Call the static method using reflection
    Map<Long, List<Long>> result = callGetUserIdToRoleIdsMap(userRoles);

    // Assert
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result).hasSize(3);

    Assertions.assertThat(result.get(1L)).containsExactlyInAnyOrder(10L, 11L);
    Assertions.assertThat(result.get(2L)).containsExactlyInAnyOrder(12L);
    Assertions.assertThat(result.get(3L)).containsExactlyInAnyOrder(13L, 14L);

    // Verify that users with no roles are not in the map
    Assertions.assertThat(result.containsKey(4L)).isFalse();
  }

  private UserRolePo createUserRolePo(Long userId, Long roleId) {
    UserRolePo userRole = new UserRolePo();
    userRole.setUserId(userId);
    userRole.setRoleId(roleId);
    return userRole;
  }

  // Helper method to access the private static method using reflection
  private Map<Long, List<Long>> callGetUserIdToRoleIdsMap(List<UserRolePo> userRoles) {
    try {
      java.lang.reflect.Method method = UserPageService.class.getDeclaredMethod("getUserIdToRoleIdsMap", List.class);
      method.setAccessible(true);
      return (Map<Long, List<Long>>) method.invoke(null, userRoles);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void should_not_set_role_ids_when_set_role_ids_is_called_with_empty_list() {
    // Arrange
    TestableUserPageService testableService = spy(new TestableUserPageService(userRoleService));

    // Act - Test the userPage method with empty list which internally calls setRoleIds
    PageQo<UserExtPo> pageQo = new PageQo<>();
    pageQo.setRecords(Collections.emptyList());
    pageQo.setTotal(0L);
    UserExtPo params = new UserExtPo();

    // Mock the page method to return the same pageQo
    doReturn(pageQo).when(testableService).page(any(), any());

    PageDto<UserDto> result = testableService.callUserPage(pageQo, params);

    // Assert
    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getList()).isEmpty();
    // When there are no users, the role service should not be called
    verify(userRoleService, never()).list(any(LambdaUpdateWrapper.class));
  }

  @Test
  void should_set_role_ids_when_set_role_ids_is_called_with_populated_list() {
    // Arrange
    TestableUserPageService testableService = spy(new TestableUserPageService(userRoleService));

    UserPo userPo1 = new UserPo();
    userPo1.setId(1L);
    userPo1.setUsername("john_doe");
    userPo1.setRealName("John Doe");
    userPo1.setPhoneNumber("1234567890");

    UserPo userPo2 = new UserPo();
    userPo2.setId(2L);
    userPo2.setUsername("jane_smith");
    userPo2.setRealName("Jane Smith");
    userPo2.setPhoneNumber("0987654321");

    List<UserExtPo> records = Arrays.asList(
        createUserExtPoFromUserPo(userPo1),
        createUserExtPoFromUserPo(userPo2)
    );

    PageQo<UserExtPo> pageQo = new PageQo<>();
    pageQo.setRecords(records);
    pageQo.setTotal(2L);

    UserExtPo params = new UserExtPo();

    // Mock the page method to return the same pageQo
    doReturn(pageQo).when(testableService).page(any(), any());

    List<UserRolePo> userRoles = Arrays.asList(
        createUserRolePo(1L, 10L),
        createUserRolePo(1L, 11L),
        createUserRolePo(2L, 12L)
    );

    when(userRoleService.list(any(LambdaUpdateWrapper.class))).thenReturn(userRoles);

    // Act
    PageDto<UserDto> result = testableService.callUserPage(pageQo, params);

    // Assert
    verify(userRoleService).list(any(LambdaUpdateWrapper.class));
    Assertions.assertThat(result.getList()).hasSize(2);
    Assertions.assertThat(result.getList().get(0).getRoleIds()).containsExactlyInAnyOrder(10L, 11L);
    Assertions.assertThat(result.getList().get(1).getRoleIds()).containsExactlyInAnyOrder(12L);
  }

  private UserExtPo createUserExtPoFromUserPo(UserPo userPo) {
    UserExtPo userExt = new UserExtPo();
    userExt.setId(userPo.getId());
    userExt.setUsername(userPo.getUsername());
    userExt.setRealName(userPo.getRealName());
    userExt.setPhoneNumber(userPo.getPhoneNumber());
    return userExt;
  }
}
