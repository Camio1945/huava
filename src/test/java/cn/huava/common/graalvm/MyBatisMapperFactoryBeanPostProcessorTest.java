/*
 * Copyright (c) 2024-present. All rights reserved.
 */

package cn.huava.common.graalvm;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.ResolvableType;

/**
 * Test for {@link MyBatisMapperFactoryBeanPostProcessor}
 *
 * @author Camio1945
 */
@ExtendWith(MockitoExtension.class)
class MyBatisMapperFactoryBeanPostProcessorTest {

  @Mock private ConfigurableBeanFactory beanFactory;

  private MyBatisMapperFactoryBeanPostProcessor processor;

  @BeforeEach
  void setUp() {
    processor = new MyBatisMapperFactoryBeanPostProcessor();
  }

  @Test
  void should_implement_necessary_interfaces() {
    assertThat(processor).isInstanceOf(MergedBeanDefinitionPostProcessor.class);
    assertThat(processor).isInstanceOf(org.springframework.beans.factory.BeanFactoryAware.class);
  }

  @Test
  void should_set_bean_factory() {
    // Given
    // When
    processor.setBeanFactory(beanFactory);

    // Then
    // Verify that the internal beanFactory field is set correctly
    // Using reflection to access the private field
    try {
      java.lang.reflect.Field beanFactoryField =
          MyBatisMapperFactoryBeanPostProcessor.class.getDeclaredField("beanFactory");
      beanFactoryField.setAccessible(true);
      ConfigurableBeanFactory actualBeanFactory =
          (ConfigurableBeanFactory) beanFactoryField.get(processor);

      assertThat(actualBeanFactory).isEqualTo(beanFactory);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      fail("Failed to access beanFactory field", e);
    }
  }

  @Test
  void should_not_process_when_bean_factory_is_null() {
    // Given
    RootBeanDefinition beanDefinition = mock(RootBeanDefinition.class);
    Class<?> beanType = Object.class;
    String beanName = "testBean";

    // When
    processor.postProcessMergedBeanDefinition(beanDefinition, beanType, beanName);

    // Then
    // No exception should be thrown and no interactions should happen
    verifyNoInteractions(beanDefinition);
  }

  @Test
  void should_process_when_bean_factory_is_set_and_mapper_factory_bean_present() throws Exception {
    // Given
    processor.setBeanFactory(beanFactory);
    RootBeanDefinition beanDefinition = mock(RootBeanDefinition.class);
    Class<?> beanType = Object.class;
    String beanName = "testBean";

    when(beanFactory.getBeanClassLoader())
        .thenReturn(Thread.currentThread().getContextClassLoader());

    // Mock the resolveMapperFactoryBeanTypeIfNecessary method to verify it gets called
    MyBatisMapperFactoryBeanPostProcessor spyProcessor = spy(processor);

    // When
    spyProcessor.postProcessMergedBeanDefinition(beanDefinition, beanType, beanName);

    // Then
    verify(beanFactory).getBeanClassLoader();
    // Verify that resolveMapperFactoryBeanTypeIfNecessary is called if the class exists in
    // classpath
    // Since we can't easily mock ClassUtils.isPresent, we'll verify based on whether the class
    // exists
    try {
      Class.forName("org.mybatis.spring.mapper.MapperFactoryBean");
      // Class exists, so the method should be called
      verify(spyProcessor).resolveMapperFactoryBeanTypeIfNecessary(beanDefinition);
    } catch (ClassNotFoundException e) {
      // Class doesn't exist in classpath, so the method shouldn't be called
      verify(spyProcessor, never()).resolveMapperFactoryBeanTypeIfNecessary(beanDefinition);
    }
  }

  @Test
  void should_resolve_mapper_factory_bean_type_if_necessary_with_non_mapper_factory_bean() {
    // Given
    RootBeanDefinition beanDefinition = mock(RootBeanDefinition.class);
    when(beanDefinition.hasBeanClass()).thenReturn(false); // Not a MapperFactoryBean

    // When
    processor.resolveMapperFactoryBeanTypeIfNecessary(beanDefinition);

    // Then
    verify(beanDefinition).hasBeanClass();
    verify(beanDefinition, never()).getResolvableType();
  }

  @Test
  void
      should_resolve_mapper_factory_bean_type_if_necessary_with_mapper_factory_bean_but_no_unresolvable_generics() {
    // Given
    RootBeanDefinition beanDefinition = mock(RootBeanDefinition.class);
    when(beanDefinition.hasBeanClass()).thenReturn(true);
    when(beanDefinition.getBeanClass()).thenReturn((Class) MapperFactoryBean.class);

    ResolvableType resolvableType = mock(ResolvableType.class);
    when(resolvableType.hasUnresolvableGenerics()).thenReturn(false);
    when(beanDefinition.getResolvableType()).thenReturn(resolvableType);

    // When
    processor.resolveMapperFactoryBeanTypeIfNecessary(beanDefinition);

    // Then
    verify(beanDefinition).hasBeanClass();
    verify(beanDefinition).getBeanClass();
    verify(resolvableType).hasUnresolvableGenerics();
  }

  @Test
  void
      should_resolve_mapper_factory_bean_type_if_necessary_with_mapper_factory_bean_and_unresolvable_generics_and_null_interface() {
    // Given
    RootBeanDefinition beanDefinition = mock(RootBeanDefinition.class);
    when(beanDefinition.hasBeanClass()).thenReturn(true);
    when(beanDefinition.getBeanClass()).thenReturn((Class) MapperFactoryBean.class);

    ResolvableType resolvableType = mock(ResolvableType.class);
    when(resolvableType.hasUnresolvableGenerics()).thenReturn(true);
    when(beanDefinition.getResolvableType()).thenReturn(resolvableType);

    org.springframework.beans.MutablePropertyValues mockPropertyValues =
        mock(org.springframework.beans.MutablePropertyValues.class);
    when(mockPropertyValues.get("mapperInterface")).thenReturn(null);
    when(beanDefinition.getPropertyValues()).thenReturn(mockPropertyValues);

    // When
    processor.resolveMapperFactoryBeanTypeIfNecessary(beanDefinition);

    // Then
    verify(beanDefinition).hasBeanClass();
    verify(beanDefinition).getBeanClass();
    verify(resolvableType).hasUnresolvableGenerics();
    verify(beanDefinition).getPropertyValues();
    verify(mockPropertyValues).get("mapperInterface");
  }

  @Test
  void
      should_resolve_mapper_factory_bean_type_if_necessary_with_mapper_factory_bean_and_unresolvable_generics_and_valid_interface() {
    // Given
    RootBeanDefinition beanDefinition = mock(RootBeanDefinition.class);
    when(beanDefinition.hasBeanClass()).thenReturn(true);
    when(beanDefinition.getBeanClass()).thenReturn((Class) MapperFactoryBean.class);

    ResolvableType resolvableType = mock(ResolvableType.class);
    when(resolvableType.hasUnresolvableGenerics()).thenReturn(true);
    when(beanDefinition.getResolvableType()).thenReturn(resolvableType);

    org.springframework.beans.MutablePropertyValues propertyValues =
        mock(org.springframework.beans.MutablePropertyValues.class);
    when(propertyValues.get("mapperInterface")).thenReturn(TestMapper.class);
    when(beanDefinition.getPropertyValues()).thenReturn(propertyValues);

    // When
    processor.resolveMapperFactoryBeanTypeIfNecessary(beanDefinition);

    // Then
    verify(beanDefinition).hasBeanClass();
    verify(beanDefinition, times(2)).getBeanClass();
    verify(resolvableType).hasUnresolvableGenerics();
    verify(beanDefinition).getPropertyValues();
    verify(propertyValues).get("mapperInterface");
    verify(beanDefinition).setConstructorArgumentValues(any(ConstructorArgumentValues.class));
    verify(beanDefinition).setTargetType(any(ResolvableType.class));
  }

  @Test
  void should_get_mapper_interface_return_null_when_bean_definition_is_null() {
    // Given
    RootBeanDefinition beanDefinition = null;

    // When
    Class<?> result = processor.getMapperInterface(beanDefinition);

    // Then
    assertThat(result).isNull();
  }

  @Test
  void should_get_mapper_interface_return_null_when_property_values_throws_exception() {
    // Given
    RootBeanDefinition beanDefinition = mock(RootBeanDefinition.class);
    when(beanDefinition.getPropertyValues()).thenThrow(new RuntimeException("Test exception"));

    // When
    Class<?> result = processor.getMapperInterface(beanDefinition);

    // Then
    assertThat(result).isNull();
  }

  @Test
  void should_get_mapper_interface_return_correct_value() {
    // Given
    RootBeanDefinition beanDefinition = mock(RootBeanDefinition.class);
    org.springframework.beans.MutablePropertyValues propertyValues =
        mock(org.springframework.beans.MutablePropertyValues.class);
    when(propertyValues.get("mapperInterface")).thenReturn(TestMapper.class);
    when(beanDefinition.getPropertyValues()).thenReturn(propertyValues);

    // When
    Class<?> result = processor.getMapperInterface(beanDefinition);

    // Then
    assertThat(result).isEqualTo(TestMapper.class);
  }

  /** Test interface for mapper testing */
  public interface TestMapper {}
}
