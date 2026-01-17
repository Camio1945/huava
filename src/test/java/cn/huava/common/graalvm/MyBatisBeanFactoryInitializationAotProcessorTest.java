/*
 * Copyright 2024-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.huava.common.graalvm;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.function.Function;
import org.apache.ibatis.annotations.SelectProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.beans.factory.aot.BeanRegistrationExcludeFilter;
import org.springframework.beans.factory.support.RegisteredBean;

/**
 * Tests for {@link MyBatisBeanFactoryInitializationAotProcessor}.
 *
 * @author Camio1945
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MyBatisBeanFactoryInitializationAotProcessorTest {

  private MyBatisBeanFactoryInitializationAotProcessor processor;

  @Mock private RuntimeHints mockHints;
  @Mock private ReflectionHints mockReflectionHints;

  @BeforeEach
  void setUp() {
    processor = new MyBatisBeanFactoryInitializationAotProcessor();
    when(mockHints.reflection()).thenReturn(mockReflectionHints);
  }

  @Test
  void should_exclude_mapper_scanner_configurer_from_aot_processing() {
    // given
    RegisteredBean registeredBean = Mockito.mock(RegisteredBean.class);
    when(registeredBean.getBeanClass()).thenReturn((Class) MapperScannerConfigurer.class);

    // when
    boolean result = processor.isExcludedFromAotProcessing(registeredBean);

    // then
    assertThat(result).isTrue();
  }

  @Test
  void should_not_exclude_other_classes_from_aot_processing() {
    // given
    RegisteredBean registeredBean = Mockito.mock(RegisteredBean.class);
    when(registeredBean.getBeanClass()).thenReturn((Class) String.class);

    // when
    boolean result = processor.isExcludedFromAotProcessing(registeredBean);

    // then
    assertThat(result).isFalse();
  }

  @Test
  void should_register_reflection_type_if_necessary_for_non_java_types() {
    // given
    Class<MyBatisBeanFactoryInitializationAotProcessor> type = MyBatisBeanFactoryInitializationAotProcessor.class;

    // when
    processor.registerReflectionTypeIfNecessary(type, mockHints);

    // then
    verify(mockHints).reflection();
  }

  @Test
  void should_not_register_reflection_type_if_necessary_for_primitive_types() {
    // given
    Class<Integer> type = int.class;

    // when
    processor.registerReflectionTypeIfNecessary(type, mockHints);

    // then
    verify(mockHints, never()).reflection();
  }

  @Test
  void should_not_register_reflection_type_if_necessary_for_java_package_types() {
    // given
    Class<String> type = String.class; // This is in java.lang package

    // when
    processor.registerReflectionTypeIfNecessary(type, mockHints);

    // then
    verify(mockHints, never()).reflection();
  }

  @Test
  void should_register_sql_provider_types() throws NoSuchMethodException {
    // given
    Method method = TestMapper.class.getMethod("testMethod");
    Class<SelectProvider> annotationType = SelectProvider.class;
    Function<SelectProvider, Class<?>> providerTypeResolver = SelectProvider::type;

    // when
    processor.registerSqlProviderTypes(method, mockHints, annotationType, providerTypeResolver);

    // then
    // The method has annotations, so the reflection should be called for the type
    verify(mockHints).reflection();
  }

  // Test interface for annotation testing
  public interface TestMapper {
    @SelectProvider(type = MyBatisBeanFactoryInitializationAotProcessor.class, method = "someMethod")
    void testMethod();
  }
}