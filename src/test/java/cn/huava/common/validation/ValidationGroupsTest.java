package cn.huava.common.validation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Test class for validation groups to ensure 100% coverage
 *
 * @author Camio1945
 */
class ValidationGroupsTest {

  @Test
  void testCreateGroup() {
    // Test that Create interface exists and is accessible
    assertNotNull(Create.class);
    assertEquals("cn.huava.common.validation.Create", Create.class.getName());
  }

  @Test
  void testUpdateGroup() {
    // Test that Update interface exists and is accessible
    assertNotNull(Update.class);
    assertEquals("cn.huava.common.validation.Update", Update.class.getName());
  }

  @Test
  void testDeleteGroup() {
    // Test that Delete interface exists and is accessible
    assertNotNull(Delete.class);
    assertEquals("cn.huava.common.validation.Delete", Delete.class.getName());
  }
}
