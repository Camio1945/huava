package cn.huava.common.graalvm;

import cn.huava.common.annotation.VisibleForTesting;
import cn.huava.common.enumeration.AccessModifierEnum;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.ibatis.reflection.TypeParameterResolver;

/**
 * Utility class for resolving MyBatis mapper types.
 *
 * @author Camio1945
 */
public class MyBatisMapperTypeUtils {
  protected MyBatisMapperTypeUtils() {
    // NOP
  }

  public static Class<?> resolveReturnClass(Class<?> mapperInterface, Method method) {
    Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, mapperInterface);
    return typeToClass(resolvedReturnType, method.getReturnType());
  }

  public static Set<Class<?>> resolveParameterClasses(Class<?> mapperInterface, Method method) {
    return Stream.of(TypeParameterResolver.resolveParamTypes(method, mapperInterface))
        .map(x -> typeToClass(x, x instanceof Class ? (Class<?>) x : Object.class))
        .collect(Collectors.toSet());
  }

  @VisibleForTesting(original = AccessModifierEnum.PRIVATE)
  protected static Class<?> typeToClass(Type src, Class<?> fallback) {
    Class<?> result = null;
    if (src instanceof Class<?>) {
      if (((Class<?>) src).isArray()) {
        result = ((Class<?>) src).getComponentType();
      } else {
        result = (Class<?>) src;
      }
    } else if (src instanceof ParameterizedType parameterizedType) {
      int index = getIndex(parameterizedType);
      Type actualType = parameterizedType.getActualTypeArguments()[index];
      result = typeToClass(actualType, fallback);
    }
    if (result == null) {
      result = fallback;
    }
    return result;
  }

  private static int getIndex(ParameterizedType parameterizedType) {
    int index;
    if (isMapWithMultipleTypeArguments(parameterizedType)) {
      index = 1;
    } else {
      index = 0;
    }
    return index;
  }

  private static boolean isMapWithMultipleTypeArguments(ParameterizedType parameterizedType) {
    return parameterizedType.getRawType() instanceof Class
        && Map.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())
        && parameterizedType.getActualTypeArguments().length > 1;
  }
}
