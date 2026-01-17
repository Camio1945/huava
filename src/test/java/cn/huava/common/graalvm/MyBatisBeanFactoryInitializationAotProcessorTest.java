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

import org.springframework.aot.hint.MemberCategory;

import java.lang.reflect.Method;
import java.util.function.Function;
import org.apache.ibatis.annotations.SelectProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
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
    Class<MyBatisBeanFactoryInitializationAotProcessor> type =
        MyBatisBeanFactoryInitializationAotProcessor.class;

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

  @Test
  void should_process_mybatis_mapper_factory_beans_with_hash_prefix() {
    // given
    ConfigurableListableBeanFactory mockBeanFactory =
        Mockito.mock(ConfigurableListableBeanFactory.class);

    // Mock bean names that include a MyBatis mapper factory bean with hash prefix
    String[] beanNames = {"#myMapper"};
    when(mockBeanFactory.getBeanNamesForType(MapperFactoryBean.class)).thenReturn(beanNames);

    // Create a mock bean definition for the actual mapper
    BeanDefinition mockBeanDefinition = Mockito.mock(BeanDefinition.class);
    when(mockBeanFactory.getBeanDefinition("myMapper")).thenReturn(mockBeanDefinition);

    // Mock the property values
    org.springframework.beans.MutablePropertyValues mockPropertyValues =
        Mockito.mock(org.springframework.beans.MutablePropertyValues.class);
    when(mockBeanDefinition.getPropertyValues()).thenReturn(mockPropertyValues);

    // Mock the property value for mapperInterface
    PropertyValue mockPropertyValue = Mockito.mock(PropertyValue.class);
    when(mockPropertyValues.getPropertyValue("mapperInterface")).thenReturn(mockPropertyValue);
    when(mockPropertyValue.getValue()).thenReturn(TestMapper.class);

    // Mock the RuntimeHints and its components
    RuntimeHints mockRuntimeHints = Mockito.mock(RuntimeHints.class);
    org.springframework.aot.hint.ProxyHints mockProxyHints = Mockito.mock(org.springframework.aot.hint.ProxyHints.class);
    org.springframework.aot.hint.ResourceHints mockResourceHints = Mockito.mock(org.springframework.aot.hint.ResourceHints.class);
    org.springframework.aot.hint.ReflectionHints mockReflectionHints = Mockito.mock(org.springframework.aot.hint.ReflectionHints.class);

    when(mockRuntimeHints.proxies()).thenReturn(mockProxyHints);
    when(mockRuntimeHints.resources()).thenReturn(mockResourceHints);
    when(mockRuntimeHints.reflection()).thenReturn(mockReflectionHints);
    when(mockProxyHints.registerJdkProxy(any(Class.class))).thenReturn(mockProxyHints);
    when(mockResourceHints.registerPattern(any(String.class))).thenReturn(mockResourceHints);
    when(mockReflectionHints.registerType(any(Class.class), any(MemberCategory[].class))).thenReturn(mockReflectionHints);

    // Mock the GenerationContext to return our runtime hints
    org.springframework.aot.generate.GenerationContext mockGenContext =
        Mockito.mock(org.springframework.aot.generate.GenerationContext.class);
    when(mockGenContext.getRuntimeHints()).thenReturn(mockRuntimeHints);

    // when
    BeanFactoryInitializationAotContribution contribution =
        processor.processAheadOfTime(mockBeanFactory);

    // then
    assertThat(contribution).isNotNull();

    // Actually invoke the contribution to execute the code path
    org.springframework.beans.factory.aot.BeanFactoryInitializationCode mockCode =
        Mockito.mock(org.springframework.beans.factory.aot.BeanFactoryInitializationCode.class);
    contribution.applyTo(mockGenContext, mockCode);

    // Verify that the proxy and resource hints were registered
    verify(mockProxyHints).registerJdkProxy(eq(TestMapper.class));
    verify(mockResourceHints).registerPattern(any(String.class));
  }

  @Test
  void should_skip_bean_names_without_hash_prefix() {
    // given
    ConfigurableListableBeanFactory mockBeanFactory =
        Mockito.mock(ConfigurableListableBeanFactory.class);

    // Mock bean names that include a MyBatis mapper factory bean without hash prefix
    String[] beanNames = {"myMapper"}; // No # prefix
    when(mockBeanFactory.getBeanNamesForType(MapperFactoryBean.class)).thenReturn(beanNames);

    // when
    BeanFactoryInitializationAotContribution contribution =
        processor.processAheadOfTime(mockBeanFactory);

    // then
    // When bean names don't start with #, the contribution should still be created but won't
    // process those beans
    // The contribution will exist but will skip the beans without # prefix
    assertThat(contribution).isNotNull();

    // Actually invoke the contribution to execute the code path
    org.springframework.aot.generate.GenerationContext mockGenContext =
        Mockito.mock(org.springframework.aot.generate.GenerationContext.class);
    org.springframework.beans.factory.aot.BeanFactoryInitializationCode mockCode =
        Mockito.mock(org.springframework.beans.factory.aot.BeanFactoryInitializationCode.class);
    contribution.applyTo(mockGenContext, mockCode);
  }

  @Test
  void should_handle_null_mapper_interface_value() {
    // given
    ConfigurableListableBeanFactory mockBeanFactory =
        Mockito.mock(ConfigurableListableBeanFactory.class);

    // Mock bean names that include a MyBatis mapper factory bean with hash prefix
    String[] beanNames = {"#myMapper"};
    when(mockBeanFactory.getBeanNamesForType(MapperFactoryBean.class)).thenReturn(beanNames);

    // Create a mock bean definition for the actual mapper
    BeanDefinition mockBeanDefinition = Mockito.mock(BeanDefinition.class);
    when(mockBeanFactory.getBeanDefinition("myMapper")).thenReturn(mockBeanDefinition);

    // Mock the property values
    org.springframework.beans.MutablePropertyValues mockPropertyValues =
        Mockito.mock(org.springframework.beans.MutablePropertyValues.class);
    when(mockBeanDefinition.getPropertyValues()).thenReturn(mockPropertyValues);

    // Mock the property value for mapperInterface to return null
    PropertyValue mockPropertyValue = Mockito.mock(PropertyValue.class);
    when(mockPropertyValues.getPropertyValue("mapperInterface")).thenReturn(mockPropertyValue);
    when(mockPropertyValue.getValue()).thenReturn(null); // This creates the branch condition

    // Mock the RuntimeHints and its components
    RuntimeHints mockRuntimeHints = Mockito.mock(RuntimeHints.class);
    org.springframework.aot.hint.ProxyHints mockProxyHints = Mockito.mock(org.springframework.aot.hint.ProxyHints.class);
    org.springframework.aot.hint.ResourceHints mockResourceHints = Mockito.mock(org.springframework.aot.hint.ResourceHints.class);
    org.springframework.aot.hint.ReflectionHints mockReflectionHints = Mockito.mock(org.springframework.aot.hint.ReflectionHints.class);

    when(mockRuntimeHints.proxies()).thenReturn(mockProxyHints);
    when(mockRuntimeHints.resources()).thenReturn(mockResourceHints);
    when(mockRuntimeHints.reflection()).thenReturn(mockReflectionHints);
    when(mockProxyHints.registerJdkProxy(any(Class.class))).thenReturn(mockProxyHints);
    when(mockResourceHints.registerPattern(any(String.class))).thenReturn(mockResourceHints);
    when(mockReflectionHints.registerType(any(Class.class), any(MemberCategory[].class))).thenReturn(mockReflectionHints);

    org.springframework.aot.generate.GenerationContext mockGenContext =
        Mockito.mock(org.springframework.aot.generate.GenerationContext.class);
    when(mockGenContext.getRuntimeHints()).thenReturn(mockRuntimeHints);

    // when
    BeanFactoryInitializationAotContribution contribution =
        processor.processAheadOfTime(mockBeanFactory);

    // then
    assertThat(contribution).isNotNull();

    // Actually invoke the contribution to execute the code path
    org.springframework.beans.factory.aot.BeanFactoryInitializationCode mockCode =
        Mockito.mock(org.springframework.beans.factory.aot.BeanFactoryInitializationCode.class);
    contribution.applyTo(mockGenContext, mockCode);
  }

  @Test
  void should_handle_mapper_interface_property_value_null() {
    // given
    ConfigurableListableBeanFactory mockBeanFactory =
        Mockito.mock(ConfigurableListableBeanFactory.class);

    // Mock bean names that include a MyBatis mapper factory bean with hash prefix
    String[] beanNames = {"#myMapper"};
    when(mockBeanFactory.getBeanNamesForType(MapperFactoryBean.class)).thenReturn(beanNames);

    // Create a mock bean definition for the actual mapper
    BeanDefinition mockBeanDefinition = Mockito.mock(BeanDefinition.class);
    when(mockBeanFactory.getBeanDefinition("myMapper")).thenReturn(mockBeanDefinition);

    // Mock the property values
    org.springframework.beans.MutablePropertyValues mockPropertyValues =
        Mockito.mock(org.springframework.beans.MutablePropertyValues.class);
    when(mockBeanDefinition.getPropertyValues()).thenReturn(mockPropertyValues);

    // Mock the property value for mapperInterface to be null
    when(mockPropertyValues.getPropertyValue("mapperInterface")).thenReturn(null);

    // Mock the RuntimeHints and its components
    RuntimeHints mockRuntimeHints = Mockito.mock(RuntimeHints.class);
    org.springframework.aot.hint.ProxyHints mockProxyHints = Mockito.mock(org.springframework.aot.hint.ProxyHints.class);
    org.springframework.aot.hint.ResourceHints mockResourceHints = Mockito.mock(org.springframework.aot.hint.ResourceHints.class);
    org.springframework.aot.hint.ReflectionHints mockReflectionHints = Mockito.mock(org.springframework.aot.hint.ReflectionHints.class);

    when(mockRuntimeHints.proxies()).thenReturn(mockProxyHints);
    when(mockRuntimeHints.resources()).thenReturn(mockResourceHints);
    when(mockRuntimeHints.reflection()).thenReturn(mockReflectionHints);
    when(mockProxyHints.registerJdkProxy(any(Class.class))).thenReturn(mockProxyHints);
    when(mockResourceHints.registerPattern(any(String.class))).thenReturn(mockResourceHints);
    when(mockReflectionHints.registerType(any(Class.class), any(MemberCategory[].class))).thenReturn(mockReflectionHints);

    org.springframework.aot.generate.GenerationContext mockGenContext =
        Mockito.mock(org.springframework.aot.generate.GenerationContext.class);
    when(mockGenContext.getRuntimeHints()).thenReturn(mockRuntimeHints);

    // when
    BeanFactoryInitializationAotContribution contribution =
        processor.processAheadOfTime(mockBeanFactory);

    // then
    assertThat(contribution).isNotNull();

    // Actually invoke the contribution to execute the code path
    org.springframework.beans.factory.aot.BeanFactoryInitializationCode mockCode =
        Mockito.mock(org.springframework.beans.factory.aot.BeanFactoryInitializationCode.class);
    contribution.applyTo(mockGenContext, mockCode);
  }

  @Test
  void should_handle_mapper_interface_type_cast_to_null() {
    // given
    ConfigurableListableBeanFactory mockBeanFactory =
        Mockito.mock(ConfigurableListableBeanFactory.class);

    // Mock bean names that include a MyBatis mapper factory bean with hash prefix
    String[] beanNames = {"#myMapper"};
    when(mockBeanFactory.getBeanNamesForType(MapperFactoryBean.class)).thenReturn(beanNames);

    // Create a mock bean definition for the actual mapper
    BeanDefinition mockBeanDefinition = Mockito.mock(BeanDefinition.class);
    when(mockBeanFactory.getBeanDefinition("myMapper")).thenReturn(mockBeanDefinition);

    // Mock the property values
    org.springframework.beans.MutablePropertyValues mockPropertyValues =
        Mockito.mock(org.springframework.beans.MutablePropertyValues.class);
    when(mockBeanDefinition.getPropertyValues()).thenReturn(mockPropertyValues);

    // Mock the property value for mapperInterface - the getValue() returns null when cast to Class
    PropertyValue mockPropertyValue = Mockito.mock(PropertyValue.class);
    when(mockPropertyValues.getPropertyValue("mapperInterface")).thenReturn(mockPropertyValue);
    when(mockPropertyValue.getValue()).thenReturn(null); // This creates the case where mapperInterfaceType becomes null after cast

    // Mock the RuntimeHints and its components
    RuntimeHints mockRuntimeHints = Mockito.mock(RuntimeHints.class);
    org.springframework.aot.hint.ProxyHints mockProxyHints = Mockito.mock(org.springframework.aot.hint.ProxyHints.class);
    org.springframework.aot.hint.ResourceHints mockResourceHints = Mockito.mock(org.springframework.aot.hint.ResourceHints.class);
    org.springframework.aot.hint.ReflectionHints mockReflectionHints = Mockito.mock(org.springframework.aot.hint.ReflectionHints.class);

    when(mockRuntimeHints.proxies()).thenReturn(mockProxyHints);
    when(mockRuntimeHints.resources()).thenReturn(mockResourceHints);
    when(mockRuntimeHints.reflection()).thenReturn(mockReflectionHints);
    when(mockProxyHints.registerJdkProxy(any(Class.class))).thenReturn(mockProxyHints);
    when(mockResourceHints.registerPattern(any(String.class))).thenReturn(mockResourceHints);
    when(mockReflectionHints.registerType(any(Class.class), any(MemberCategory[].class))).thenReturn(mockReflectionHints);

    org.springframework.aot.generate.GenerationContext mockGenContext =
        Mockito.mock(org.springframework.aot.generate.GenerationContext.class);
    when(mockGenContext.getRuntimeHints()).thenReturn(mockRuntimeHints);

    // when
    BeanFactoryInitializationAotContribution contribution =
        processor.processAheadOfTime(mockBeanFactory);

    // then
    assertThat(contribution).isNotNull();

    // Actually invoke the contribution to execute the code path
    org.springframework.beans.factory.aot.BeanFactoryInitializationCode mockCode =
        Mockito.mock(org.springframework.beans.factory.aot.BeanFactoryInitializationCode.class);
    contribution.applyTo(mockGenContext, mockCode);
  }


  @Test
  void should_handle_null_mapper_interface_type() {
    // given
    ConfigurableListableBeanFactory mockBeanFactory =
        Mockito.mock(ConfigurableListableBeanFactory.class);

    // Mock bean names that include a MyBatis mapper factory bean with hash prefix
    String[] beanNames = {"#myMapper"};
    when(mockBeanFactory.getBeanNamesForType(MapperFactoryBean.class)).thenReturn(beanNames);

    // Create a mock bean definition for the actual mapper
    BeanDefinition mockBeanDefinition = Mockito.mock(BeanDefinition.class);
    when(mockBeanFactory.getBeanDefinition("myMapper")).thenReturn(mockBeanDefinition);

    // Mock the property values
    org.springframework.beans.MutablePropertyValues mockPropertyValues =
        Mockito.mock(org.springframework.beans.MutablePropertyValues.class);
    when(mockBeanDefinition.getPropertyValues()).thenReturn(mockPropertyValues);

    // Mock the property value for mapperInterface with a null value
    PropertyValue mockPropertyValue = Mockito.mock(PropertyValue.class);
    when(mockPropertyValues.getPropertyValue("mapperInterface")).thenReturn(mockPropertyValue);
    when(mockPropertyValue.getValue()).thenReturn(null);

    // Mock the RuntimeHints and its components
    RuntimeHints mockRuntimeHints = Mockito.mock(RuntimeHints.class);
    org.springframework.aot.generate.GenerationContext mockGenContext =
        Mockito.mock(org.springframework.aot.generate.GenerationContext.class);
    when(mockGenContext.getRuntimeHints()).thenReturn(mockRuntimeHints);

    // when
    BeanFactoryInitializationAotContribution contribution =
        processor.processAheadOfTime(mockBeanFactory);

    // then
    assertThat(contribution).isNotNull();

    // Actually invoke the contribution to execute the code path
    org.springframework.beans.factory.aot.BeanFactoryInitializationCode mockCode =
        Mockito.mock(org.springframework.beans.factory.aot.BeanFactoryInitializationCode.class);
    contribution.applyTo(mockGenContext, mockCode);
  }

  @Test
  void should_handle_exception_when_accessing_bean_definition() {
    // given
    ConfigurableListableBeanFactory mockBeanFactory =
        Mockito.mock(ConfigurableListableBeanFactory.class);

    // Mock bean names that include a MyBatis mapper factory bean with hash prefix
    String[] beanNames = {"#myMapper"};
    when(mockBeanFactory.getBeanNamesForType(MapperFactoryBean.class)).thenReturn(beanNames);

    // Throw an exception when trying to get the bean definition
    when(mockBeanFactory.getBeanDefinition("myMapper"))
        .thenThrow(new RuntimeException("Test exception"));

    // when
    BeanFactoryInitializationAotContribution contribution =
        processor.processAheadOfTime(mockBeanFactory);

    // then
    // The contribution should still be created even if there's an exception accessing a bean
    // definition
    // The exception handling should prevent the process from failing
    assertThat(contribution).isNotNull();

    // Actually invoke the contribution to execute the code path
    org.springframework.aot.generate.GenerationContext mockGenContext =
        Mockito.mock(org.springframework.aot.generate.GenerationContext.class);
    org.springframework.beans.factory.aot.BeanFactoryInitializationCode mockCode =
        Mockito.mock(org.springframework.beans.factory.aot.BeanFactoryInitializationCode.class);
    contribution.applyTo(mockGenContext, mockCode);
  }

  @Test
  void should_return_null_when_no_mapper_factory_beans_exist() {
    // given
    ConfigurableListableBeanFactory mockBeanFactory =
        Mockito.mock(ConfigurableListableBeanFactory.class);
    when(mockBeanFactory.getBeanNamesForType(MapperFactoryBean.class)).thenReturn(new String[] {});

    // when
    BeanFactoryInitializationAotContribution contribution =
        processor.processAheadOfTime(mockBeanFactory);

    // then
    assertThat(contribution).isNull();
  }

  @Test
  void should_handle_exception_during_bean_definition_access() {
    // given
    ConfigurableListableBeanFactory mockBeanFactory =
        Mockito.mock(ConfigurableListableBeanFactory.class);

    // Mock bean names that include a MyBatis mapper factory bean with hash prefix
    String[] beanNames = {"#myMapper"};
    when(mockBeanFactory.getBeanNamesForType(MapperFactoryBean.class)).thenReturn(beanNames);

    // Make the getBeanDefinition call throw an exception
    when(mockBeanFactory.getBeanDefinition("myMapper")).thenThrow(new RuntimeException("Test exception"));

    // Mock the RuntimeHints and its components
    RuntimeHints mockRuntimeHints = Mockito.mock(RuntimeHints.class);
    org.springframework.aot.hint.ProxyHints mockProxyHints = Mockito.mock(org.springframework.aot.hint.ProxyHints.class);
    org.springframework.aot.hint.ResourceHints mockResourceHints = Mockito.mock(org.springframework.aot.hint.ResourceHints.class);
    org.springframework.aot.hint.ReflectionHints mockReflectionHints = Mockito.mock(org.springframework.aot.hint.ReflectionHints.class);

    when(mockRuntimeHints.proxies()).thenReturn(mockProxyHints);
    when(mockRuntimeHints.resources()).thenReturn(mockResourceHints);
    when(mockRuntimeHints.reflection()).thenReturn(mockReflectionHints);
    when(mockProxyHints.registerJdkProxy(any(Class.class))).thenReturn(mockProxyHints);
    when(mockResourceHints.registerPattern(any(String.class))).thenReturn(mockResourceHints);
    when(mockReflectionHints.registerType(any(Class.class), any(MemberCategory[].class))).thenReturn(mockReflectionHints);

    org.springframework.aot.generate.GenerationContext mockGenContext =
        Mockito.mock(org.springframework.aot.generate.GenerationContext.class);
    when(mockGenContext.getRuntimeHints()).thenReturn(mockRuntimeHints);

    // when
    BeanFactoryInitializationAotContribution contribution =
        processor.processAheadOfTime(mockBeanFactory);

    // then
    assertThat(contribution).isNotNull();

    // Actually invoke the contribution to execute the code path
    org.springframework.beans.factory.aot.BeanFactoryInitializationCode mockCode =
        Mockito.mock(org.springframework.beans.factory.aot.BeanFactoryInitializationCode.class);
    contribution.applyTo(mockGenContext, mockCode);
  }

  // Test interface for annotation testing
  public interface TestMapper {
    @SelectProvider(
        type = MyBatisBeanFactoryInitializationAotProcessor.class,
        method = "someMethod")
    void testMethod();
  }
}
