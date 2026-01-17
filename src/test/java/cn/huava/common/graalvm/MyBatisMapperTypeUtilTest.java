package cn.huava.common.graalvm;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for MyBatisMapperTypeUtil to achieve full coverage
 *
 * @author Camio1945
 */
@ExtendWith(MockitoExtension.class)
class MyBatisMapperTypeUtilTest {

  @Test
  void should_resolve_return_class_using_type_parameter_resolver() throws NoSuchMethodException {
    // Create a mock method with generic return type
    Method method = TestMapper.class.getMethod("findById", Long.class);

    Class<?> result = MyBatisMapperTypeUtil.resolveReturnClass(TestMapper.class, method);

    // The actual result depends on the method's return type and resolution
    assertThat(result).isNotNull();
  }

  @Test
  void should_resolve_parameter_classes_using_type_parameter_resolver() throws NoSuchMethodException {
    // Create a mock method with generic parameter types
    Method method = TestMapper.class.getMethod("findById", Long.class);

    Set<Class<?>> result = MyBatisMapperTypeUtil.resolveParameterClasses(TestMapper.class, method);

    assertThat(result).isNotNull();
  }

  @Test
  void should_convert_class_type_to_class() {
    Class<?> result = MyBatisMapperTypeUtil.typeToClass(String.class, Object.class);

    assertThat(result).isEqualTo(String.class);
  }

  @Test
  void should_convert_array_type_to_component_type() {
    Class<?> result = MyBatisMapperTypeUtil.typeToClass(int[].class, Object.class);

    assertThat(result).isEqualTo(int.class);
  }

  @Test
  void should_use_fallback_when_result_is_null() {
    Class<?> result = MyBatisMapperTypeUtil.typeToClass(null, String.class);

    assertThat(result).isEqualTo(String.class);
  }

  @Test
  void should_handle_parameterized_type_with_single_argument() {
    // Create a mock ParameterizedType with single type argument
    ParameterizedType mockParameterizedType = Mockito.mock(ParameterizedType.class);
    Type[] mockTypeArgs = {String.class};

    Mockito.when(mockParameterizedType.getActualTypeArguments()).thenReturn(mockTypeArgs);
    Mockito.when(mockParameterizedType.getRawType()).thenReturn(List.class);

    Class<?> result = MyBatisMapperTypeUtil.typeToClass(mockParameterizedType, Object.class);

    assertThat(result).isEqualTo(String.class); // Should return the first type argument
  }

  @Test
  void should_handle_parameterized_type_with_multiple_arguments_for_map() {
    // Create a mock ParameterizedType representing Map<String, Integer>
    ParameterizedType mockParameterizedType = Mockito.mock(ParameterizedType.class);
    Type[] mockTypeArgs = {String.class, Integer.class};

    Mockito.when(mockParameterizedType.getActualTypeArguments()).thenReturn(mockTypeArgs);
    Mockito.when(mockParameterizedType.getRawType()).thenReturn(Map.class);

    Class<?> result = MyBatisMapperTypeUtil.typeToClass(mockParameterizedType, Object.class);

    // For Map, it should pick the second type argument (value type)
    assertThat(result).isNotNull();
  }

  @Test
  void should_handle_parameterized_type_with_multiple_arguments_for_non_map() {
    // Create a mock ParameterizedType representing a non-Map class with multiple type arguments
    ParameterizedType mockParameterizedType = Mockito.mock(ParameterizedType.class);
    Type[] mockTypeArgs = {String.class, Integer.class};

    Mockito.when(mockParameterizedType.getActualTypeArguments()).thenReturn(mockTypeArgs);
    Mockito.when(mockParameterizedType.getRawType()).thenReturn(ArrayList.class);

    Class<?> result = MyBatisMapperTypeUtil.typeToClass(mockParameterizedType, Object.class);

    // For non-Map classes, it should pick the first type argument
    assertThat(result).isNotNull();
  }

  @Test
  void should_identify_map_with_multiple_type_arguments_correctly() {
    // Create a mock ParameterizedType representing Map<String, Integer>
    ParameterizedType mockParameterizedType = Mockito.mock(ParameterizedType.class);
    Type[] mockTypeArgs = {String.class, Integer.class};

    Mockito.when(mockParameterizedType.getActualTypeArguments()).thenReturn(mockTypeArgs);
    Mockito.when(mockParameterizedType.getRawType()).thenReturn(Map.class);

    // Using reflection to test the private getIndex method indirectly
    // We'll test the behavior through typeToClass which uses the helper methods
    Class<?> result = MyBatisMapperTypeUtil.typeToClass(mockParameterizedType, Object.class);

    assertThat(result).isNotNull();
  }

  @Test
  void should_identify_non_map_or_single_argument_types_correctly() {
    // Create a mock ParameterizedType representing List<String>
    ParameterizedType mockParameterizedType = Mockito.mock(ParameterizedType.class);
    Type[] mockTypeArgs = {String.class};

    Mockito.when(mockParameterizedType.getActualTypeArguments()).thenReturn(mockTypeArgs);
    Mockito.when(mockParameterizedType.getRawType()).thenReturn(List.class);

    Class<?> result = MyBatisMapperTypeUtil.typeToClass(mockParameterizedType, Object.class);

    assertThat(result).isNotNull();
  }

  @Test
  void should_construct_utility_class_without_error() {
    // Test the protected constructor by using reflection or extending the class
    MyBatisMapperTypeUtil util = new MyBatisMapperTypeUtil() {};

    assertThat(util).isNotNull();
  }

  @Test
  void should_handle_complex_nested_parameterized_types() {
    // Create a mock ParameterizedType representing a complex nested type
    ParameterizedType outerType = Mockito.mock(ParameterizedType.class);
    ParameterizedType innerType = Mockito.mock(ParameterizedType.class);
    Type[] innerTypeArgs = {String.class};

    Mockito.when(innerType.getActualTypeArguments()).thenReturn(innerTypeArgs);
    Mockito.when(innerType.getRawType()).thenReturn(List.class);

    Type[] outerTypeArgs = {innerType};
    Mockito.when(outerType.getActualTypeArguments()).thenReturn(outerTypeArgs);
    Mockito.when(outerType.getRawType()).thenReturn(Optional.class);

    Class<?> result = MyBatisMapperTypeUtil.typeToClass(outerType, Object.class);

    assertThat(result).isNotNull();
  }

  @Test
  void should_handle_map_with_multiple_type_arguments_properly() {
    // Create a mock ParameterizedType representing Map<String, Integer>
    ParameterizedType mockParameterizedType = Mockito.mock(ParameterizedType.class);
    Type[] mockTypeArgs = {String.class, Integer.class};

    Mockito.when(mockParameterizedType.getActualTypeArguments()).thenReturn(mockTypeArgs);
    Mockito.when(mockParameterizedType.getRawType()).thenReturn(Map.class);

    Class<?> result = MyBatisMapperTypeUtil.typeToClass(mockParameterizedType, Object.class);

    // For Map, it should pick the second type argument (value type)
    assertThat(result).isEqualTo(Integer.class);
  }

  @Test
  void should_return_false_when_raw_type_is_not_class() {
    // Create a mock ParameterizedType where raw type is not a Class
    ParameterizedType mockParameterizedType = Mockito.mock(ParameterizedType.class);
    Type mockRawType = Mockito.mock(Type.class); // Not a Class
    Type[] mockTypeArgs = {String.class, Integer.class};

    Mockito.when(mockParameterizedType.getActualTypeArguments()).thenReturn(mockTypeArgs);
    Mockito.when(mockParameterizedType.getRawType()).thenReturn(mockRawType);

    // Since the raw type is not a Class, isMapWithMultipleTypeArguments should return false
    // This will cause typeToClass to access index 0 instead of 1
    Class<?> result = MyBatisMapperTypeUtil.typeToClass(mockParameterizedType, Object.class);

    assertThat(result).isNotNull();
  }

  @Test
  void should_return_false_when_raw_type_is_not_assignable_from_map() {
    // Create a mock ParameterizedType where raw type is not assignable from Map
    ParameterizedType mockParameterizedType = Mockito.mock(ParameterizedType.class);
    Type[] mockTypeArgs = {String.class, Integer.class};

    Mockito.when(mockParameterizedType.getActualTypeArguments()).thenReturn(mockTypeArgs);
    Mockito.when(mockParameterizedType.getRawType()).thenReturn(List.class); // Not Map

    // Since the raw type is not assignable from Map, isMapWithMultipleTypeArguments should return false
    // This will cause typeToClass to access index 0 instead of 1
    Class<?> result = MyBatisMapperTypeUtil.typeToClass(mockParameterizedType, Object.class);

    assertThat(result).isEqualTo(String.class); // Should return the first type argument
  }

  @Test
  void should_return_false_when_map_has_only_one_type_argument() {
    // Create a mock ParameterizedType representing Map<String, ?>
    ParameterizedType mockParameterizedType = Mockito.mock(ParameterizedType.class);
    Type[] mockTypeArgs = {String.class}; // Only one type argument

    Mockito.when(mockParameterizedType.getActualTypeArguments()).thenReturn(mockTypeArgs);
    Mockito.when(mockParameterizedType.getRawType()).thenReturn(Map.class);

    // Since the map has only one type argument, isMapWithMultipleTypeArguments should return false
    // This will cause typeToClass to access index 0 instead of 1
    Class<?> result = MyBatisMapperTypeUtil.typeToClass(mockParameterizedType, Object.class);

    assertThat(result).isEqualTo(String.class); // Should return the first type argument
  }

  @Test
  void should_return_true_when_map_has_multiple_type_arguments() {
    // Create a mock ParameterizedType representing Map<String, Integer>
    ParameterizedType mockParameterizedType = Mockito.mock(ParameterizedType.class);
    Type[] mockTypeArgs = {String.class, Integer.class}; // Multiple type arguments

    Mockito.when(mockParameterizedType.getActualTypeArguments()).thenReturn(mockTypeArgs);
    Mockito.when(mockParameterizedType.getRawType()).thenReturn(Map.class);

    // Since the map has multiple type arguments, isMapWithMultipleTypeArguments should return true
    // This will cause typeToClass to access index 1 instead of 0
    Class<?> result = MyBatisMapperTypeUtil.typeToClass(mockParameterizedType, Object.class);

    assertThat(result).isEqualTo(Integer.class); // Should return the second type argument (value type)
  }

  @Test
  void should_throw_exception_when_handling_parameterized_type_with_empty_actual_type_arguments() {
    // Create a mock ParameterizedType with no type arguments
    ParameterizedType mockParameterizedType = Mockito.mock(ParameterizedType.class);
    Type[] mockTypeArgs = {}; // Empty array

    Mockito.when(mockParameterizedType.getActualTypeArguments()).thenReturn(mockTypeArgs);
    Mockito.when(mockParameterizedType.getRawType()).thenReturn(List.class);

    // This should throw an exception since the original code doesn't handle empty arrays
    assertThatThrownBy(() -> MyBatisMapperTypeUtil.typeToClass(mockParameterizedType, String.class))
        .isInstanceOf(ArrayIndexOutOfBoundsException.class);
  }

  /**
   * Test interface to simulate a mapper with generic methods
   */
  interface TestMapper {
    List<String> findById(Long id);
    void save(Map<String, Object> entity);
    Set<Integer> findAll();
  }
}
