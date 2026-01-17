package cn.huava;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Test class for {@link HuavaApplication}
 *
 * @author Camio1945
 */
@ExtendWith(MockitoExtension.class)
class HuavaApplicationTest {

  @Test
  void should_execute_main_method_without_exception() {
    // Given
    String[] args = {};

    try (MockedStatic<SpringApplication> mockedStatic = mockStatic(SpringApplication.class)) {
      // When
      HuavaApplication.main(args);

      // Then
      mockedStatic.verify(() -> SpringApplication.run(HuavaApplication.class, args));
    }
  }

  @Test
  void should_create_instance_through_private_constructor() throws Exception {
    // Testing private constructor using reflection
    var constructor = HuavaApplication.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    // When
    var instance = constructor.newInstance();

    // Then
    assertThat(instance).isNotNull();
  }

  @Test
  void should_verify_spring_boot_annotation_configuration() {
    // Then
    assertThat(HuavaApplication.class.isAnnotationPresent(SpringBootApplication.class)).isTrue();
  }
}
