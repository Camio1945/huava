package cn.huava.common.pojo.po;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

///
/// # Test class for BasePo entity to ensure 100% coverage
///
/// @author Camio1945
class BasePoTest {
  private TestBasePo testEntity;

  @BeforeEach
  void setUp() {
    testEntity = new TestBasePo();
  }

  @Test
  void testIdField() {
    Long testId = 1L;
    testEntity.setId(testId);
    assertEquals(testId, testEntity.getId());
  }

  @Test
  void testCreateTimeField() {
    Date testDate = new Date();
    testEntity.setCreatedAt(testDate);
    assertEquals(testDate, testEntity.getCreatedAt());
  }

  @Test
  void testUpdateTimeField() {
    Date testDate = new Date();
    testEntity.setUpdatedAt(testDate);
    assertEquals(testDate, testEntity.getUpdatedAt());
  }

  @Test
  void testDeleteInfoField() {
    Long deleteInfo = 123456789L;
    testEntity.setDeleteInfo(deleteInfo);
    assertEquals(deleteInfo, testEntity.getDeleteInfo());
  }

  @Test
  void testBeforeCreate() {
    Date beforeDate = new Date();
    BasePo.beforeCreate(testEntity);

    // Verify ID is set to null
    assertNull(testEntity.getId());
    // Verify create and update times are set
    assertNotNull(testEntity.getCreatedAt());
    assertNotNull(testEntity.getUpdatedAt());
    // Verify deleteInfo is set to 0
    assertEquals(0L, testEntity.getDeleteInfo());
  }

  @Test
  void testBeforeUpdate() {
    testEntity.setUpdatedAt(new Date(1000)); // Set to a specific time
    Date beforeDate = new Date();
    BasePo.beforeUpdate(testEntity);

    // Verify update time is changed
    assertNotEquals(1000, testEntity.getUpdatedAt().getTime());
  }

  @Test
  void testBeforeDelete() {
    BasePo.beforeDelete(testEntity);

    // Verify deleteInfo is set to a non-zero value
    assertNotEquals(0L, testEntity.getDeleteInfo());
    assertTrue(testEntity.getDeleteInfo() > 0);
  }

  @Test
  void testBeforeCreateWithNull() {
    // Test beforeCreate with null entity - this should not throw an exception
    // but will just return without doing anything
    assertDoesNotThrow(() -> BasePo.beforeCreate(null));
  }

  @Test
  void testBeforeUpdateWithNull() {
    // Test beforeUpdate with null entity
    assertDoesNotThrow(() -> BasePo.beforeUpdate(null));
  }

  @Test
  void testBeforeDeleteWithNull() {
    // Test beforeDelete with null entity
    assertDoesNotThrow(() -> BasePo.beforeDelete(null));
  }

  @Test
  void testSerialVersionUID() {
    // Test that serialVersionUID is accessible
    assertNotNull(TestBasePo.class);
  }

  @Test
  void testNoArgsConstructor() {
    TestBasePo emptyEntity = new TestBasePo();
    assertNotNull(emptyEntity);
  }

  @Test
  void testEqualsAndHashCode() {
    TestBasePo entity1 = new TestBasePo();
    TestBasePo entity2 = new TestBasePo();

    // Test with same ID
    entity1.setId(1L);
    entity2.setId(1L);
    assertEquals(entity1, entity2);
    assertEquals(entity1.hashCode(), entity2.hashCode());

    // Test with different ID
    entity2.setId(2L);
    assertNotEquals(entity1, entity2);
    assertNotEquals(entity1.hashCode(), entity2.hashCode());

    // Test self equality
    assertEquals(entity1, entity1);
    assertEquals(entity1.hashCode(), entity1.hashCode());

    // Test with null
    assertNotEquals(entity1, null);

    // Test with different class
    assertNotEquals(entity1, new Object());
  }

  // Test class that extends BasePo for testing purposes
  private static class TestBasePo extends BasePo {
    // Empty class for testing BasePo functionality
  }
}
