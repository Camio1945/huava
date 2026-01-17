package cn.huava.common.enumeration;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for AccessModifierEnum
 *
 * @author Camio1945
 */
@ExtendWith(MockitoExtension.class)
class AccessModifierEnumTest {

  @Test
  void should_have_private_enum_value() {
    // Given
    AccessModifierEnum accessModifier = AccessModifierEnum.PRIVATE;

    // Then
    assertThat(accessModifier).isEqualTo(AccessModifierEnum.PRIVATE);
    assertThat(accessModifier.name()).isEqualTo("PRIVATE");
  }

  @Test
  void should_have_protected_enum_value() {
    // Given
    AccessModifierEnum accessModifier = AccessModifierEnum.PROTECTED;

    // Then
    assertThat(accessModifier).isEqualTo(AccessModifierEnum.PROTECTED);
    assertThat(accessModifier.name()).isEqualTo("PROTECTED");
  }
}
