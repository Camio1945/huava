package cn.huava.sys.enumeration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Test class for UserGenderEnum to ensure 100% coverage
 *
 * @author Camio1945
 */
class UserGenderEnumTest {

  @Test
  void testEnumValues() {
    // Test that all enum values exist
    assertEquals("M", UserGenderEnum.M.name());
    assertEquals("F", UserGenderEnum.F.name());
    assertEquals("U", UserGenderEnum.U.name());

    // Test that values are accessible
    assertNotNull(UserGenderEnum.M);
    assertNotNull(UserGenderEnum.F);
    assertNotNull(UserGenderEnum.U);

    // Test values() method
    UserGenderEnum[] values = UserGenderEnum.values();
    assertEquals(3, values.length);
    assertTrue(java.util.Arrays.asList(values).contains(UserGenderEnum.M));
    assertTrue(java.util.Arrays.asList(values).contains(UserGenderEnum.F));
    assertTrue(java.util.Arrays.asList(values).contains(UserGenderEnum.U));

    // Test valueOf() method
    assertEquals(UserGenderEnum.M, UserGenderEnum.valueOf("M"));
    assertEquals(UserGenderEnum.F, UserGenderEnum.valueOf("F"));
    assertEquals(UserGenderEnum.U, UserGenderEnum.valueOf("U"));
  }

  @Test
  void testEnumOrdinal() {
    // Test ordinal values
    assertEquals(0, UserGenderEnum.M.ordinal());
    assertEquals(1, UserGenderEnum.F.ordinal());
    assertEquals(2, UserGenderEnum.U.ordinal());
  }
}
