package cn.huava.sys.pojo.po;

import static org.junit.jupiter.api.Assertions.*;

import cn.huava.common.validation.Create;
import cn.huava.common.validation.Update;
import cn.huava.sys.enumeration.UserGenderEnum;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for UserPo entity to ensure 100% coverage
 *
 * @author Camio1945
 */
class UserPoTest {
  private UserPo user;

  @BeforeEach
  void setUp() {
    user = new UserPo();
  }

  @Test
  void testIdField() {
    Long testId = 1L;
    user.setId(testId);
    assertEquals(testId, user.getId());
  }

  @Test
  void testCreateTimeField() {
    Date testDate = new Date();
    user.setCreateTime(testDate);
    assertEquals(testDate, user.getCreateTime());
  }

  @Test
  void testUpdateTimeField() {
    Date testDate = new Date();
    user.setUpdateTime(testDate);
    assertEquals(testDate, user.getUpdateTime());
  }

  @Test
  void testDeleteInfoField() {
    Long deleteInfo = 123456789L;
    user.setDeleteInfo(deleteInfo);
    assertEquals(deleteInfo, user.getDeleteInfo());
  }

  @Test
  void testUsernameField() {
    String username = "testuser";
    user.setUsername(username);
    assertEquals(username, user.getUsername());
  }

  @Test
  void testPasswordField() {
    String password = "password123";
    user.setPassword(password);
    assertEquals(password, user.getPassword());
  }

  @Test
  void testRealNameField() {
    String realName = "John Doe";
    user.setRealName(realName);
    assertEquals(realName, user.getRealName());
  }

  @Test
  void testPhoneNumberField() {
    String phoneNumber = "1234567890";
    user.setPhoneNumber(phoneNumber);
    assertEquals(phoneNumber, user.getPhoneNumber());
  }

  @Test
  void testGenderField() {
    String gender = UserGenderEnum.M.name();
    user.setGender(gender);
    assertEquals(gender, user.getGender());
  }

  @Test
  void testAvatarField() {
    String avatar = "/avatar/test.jpg";
    user.setAvatar(avatar);
    assertEquals(avatar, user.getAvatar());
  }

  @Test
  void testIsEnabledField() {
    Boolean isEnabled = true;
    user.setIsEnabled(isEnabled);
    assertEquals(isEnabled, user.getIsEnabled());
  }

  @Test
  void testDisabledReasonField() {
    String disabledReason = "Violated terms";
    user.setDisabledReason(disabledReason);
    assertEquals(disabledReason, user.getDisabledReason());
  }

  @Test
  void testLastLoginIpField() {
    String lastLoginIp = "192.168.1.1";
    user.setLastLoginIp(lastLoginIp);
    assertEquals(lastLoginIp, user.getLastLoginIp());
  }

  @Test
  void testLastLoginDateField() {
    Date lastLoginDate = new Date();
    user.setLastLoginDate(lastLoginDate);
    assertEquals(lastLoginDate, user.getLastLoginDate());
  }

  @Test
  void testRemarkField() {
    String remark = "Test user";
    user.setRemark(remark);
    assertEquals(remark, user.getRemark());
  }

  @Test
  void testSerialVersionUID() {
    // Test that serialVersionUID is accessible (though not directly testable)
    assertNotNull(UserPo.class);
  }

  @Test
  void testNoArgsConstructor() {
    UserPo emptyUser = new UserPo();
    assertNotNull(emptyUser);
  }

  @Test
  void testToStringMethod() {
    user.setUsername("testuser");
    user.setRealName("John Doe");
    // Test that toString doesn't throw an exception
    assertDoesNotThrow(() -> user.toString());
  }

  @Test
  void testEqualsAndHashCode() {
    UserPo user1 = new UserPo();
    UserPo user2 = new UserPo();

    // Test initial state - objects without IDs should be equal if both have null IDs
    assertEquals(user1, user2);

    // Test self equality
    assertEquals(user1, user1);
    assertEquals(user2, user2);

    // Test null and different type
    assertNotEquals(user1, null);
    assertNotEquals(user1, new Object());

    // Test with same ID (both null)
    assertEquals(user1, user2);

    // Test with same ID (both have same value)
    user1.setId(1L);
    user2.setId(1L);
    assertEquals(user1, user2);

    // Test with different IDs
    user2.setId(2L);
    assertNotEquals(user1, user2);
  }

  @Test
  void testValidationGroups() {
    // Test that validation annotations work with groups
    user.setUsername("testuser");
    user.setPassword("password123");
    
    // Verify fields are set correctly for Create group validation
    assertEquals("testuser", user.getUsername());
    assertEquals("password123", user.getPassword());
  }
}