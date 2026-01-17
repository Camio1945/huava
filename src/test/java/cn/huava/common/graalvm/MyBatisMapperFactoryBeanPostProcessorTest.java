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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.ResolvableType;

/**
 * Tests for {@link MyBatisMapperFactoryBeanPostProcessor}.
 *
 * @author Camio1945
 */
@ExtendWith(MockitoExtension.class)
class MyBatisMapperFactoryBeanPostProcessorTest {

  @Mock private ConfigurableBeanFactory mockBeanFactory;

  private MyBatisMapperFactoryBeanPostProcessor processor;

  @BeforeEach
  void setUp() {
    processor = new MyBatisMapperFactoryBeanPostProcessor();
  }

  @Test
  void should_set_bean_factory() {
    // when
    processor.setBeanFactory(mockBeanFactory);

    // then
    assertThat(processor).extracting("beanFactory").isEqualTo(mockBeanFactory);
  }

  @Test
  void should_not_process_merged_bean_definition_when_bean_factory_is_null() {
    // given
    RootBeanDefinition mockBeanDefinition = mock(RootBeanDefinition.class);
    Class<?> mockBeanType = Object.class;
    String beanName = "testBean";

    // when
    processor.postProcessMergedBeanDefinition(mockBeanDefinition, mockBeanType, beanName);

    // then
    verify(mockBeanDefinition, never()).hasBeanClass();
  }

  @Test
  void should_process_merged_bean_definition_when_bean_factory_is_set() {
    // given
    processor.setBeanFactory(mockBeanFactory);
    RootBeanDefinition mockBeanDefinition = mock(RootBeanDefinition.class);
    Class<?> mockBeanType = Object.class;
    String beanName = "testBean";

    // when
    processor.postProcessMergedBeanDefinition(mockBeanDefinition, mockBeanType, beanName);

    // then
    verify(mockBeanDefinition).hasBeanClass();
  }

  @Test
  void should_not_resolve_mapper_factory_bean_type_when_bean_has_no_class() {
    // given
    processor.setBeanFactory(mockBeanFactory);
    RootBeanDefinition mockBeanDefinition = mock(RootBeanDefinition.class);
    when(mockBeanDefinition.hasBeanClass()).thenReturn(false);

    // when
    processor.resolveMapperFactoryBeanTypeIfNecessary(mockBeanDefinition);

    // then
    verify(mockBeanDefinition, never()).getBeanClass();
  }

  @Test
  void should_not_resolve_mapper_factory_bean_type_when_bean_class_is_not_mapper_factory_bean() {
    // given
    processor.setBeanFactory(mockBeanFactory);
    RootBeanDefinition mockBeanDefinition = mock(RootBeanDefinition.class);
    when(mockBeanDefinition.hasBeanClass()).thenReturn(true);
    when(mockBeanDefinition.getBeanClass()).thenReturn((Class) Object.class);

    // when
    processor.resolveMapperFactoryBeanTypeIfNecessary(mockBeanDefinition);

    // then
    verify(mockBeanDefinition, never()).getResolvableType();
  }

  @Test
  void
      should_not_resolve_mapper_factory_bean_type_when_bean_class_is_mapper_factory_bean_but_generics_are_resolvable() {
    // given
    processor.setBeanFactory(mockBeanFactory);
    RootBeanDefinition mockBeanDefinition = mock(RootBeanDefinition.class);
    when(mockBeanDefinition.hasBeanClass()).thenReturn(true);
    when(mockBeanDefinition.getBeanClass()).thenReturn((Class) MapperFactoryBean.class);
    ResolvableType mockResolvableType = mock(ResolvableType.class);
    when(mockBeanDefinition.getResolvableType()).thenReturn(mockResolvableType);
    when(mockResolvableType.hasUnresolvableGenerics()).thenReturn(false);

    // when
    processor.resolveMapperFactoryBeanTypeIfNecessary(mockBeanDefinition);

    // then
    verify(mockBeanDefinition, never()).getPropertyValues();
  }

  @Test
  void
      should_resolve_mapper_factory_bean_type_when_bean_class_is_mapper_factory_bean_and_generics_are_unresolvable() {
    // given
    processor.setBeanFactory(mockBeanFactory);
    RootBeanDefinition mockBeanDefinition = mock(RootBeanDefinition.class);
    when(mockBeanDefinition.hasBeanClass()).thenReturn(true);
    when(mockBeanDefinition.getBeanClass()).thenReturn((Class) MapperFactoryBean.class);
    ResolvableType mockResolvableType = mock(ResolvableType.class);
    when(mockBeanDefinition.getResolvableType()).thenReturn(mockResolvableType);
    when(mockResolvableType.hasUnresolvableGenerics()).thenReturn(true);
    Class<?> mockMapperInterface = String.class;
    MutablePropertyValues propertyValues = new MutablePropertyValues();
    propertyValues.addPropertyValue(new PropertyValue("mapperInterface", mockMapperInterface));
    when(mockBeanDefinition.getPropertyValues()).thenReturn(propertyValues);

    // when
    processor.resolveMapperFactoryBeanTypeIfNecessary(mockBeanDefinition);

    // then
    verify(mockBeanDefinition).setConstructorArgumentValues(any(ConstructorArgumentValues.class));
    verify(mockBeanDefinition).setTargetType(any(ResolvableType.class));
  }

  @Test
  void
      should_not_resolve_mapper_factory_bean_type_when_bean_class_is_mapper_factory_bean_and_generics_are_unresolvable_but_mapper_interface_is_null() {
    // given
    processor.setBeanFactory(mockBeanFactory);
    RootBeanDefinition mockBeanDefinition = mock(RootBeanDefinition.class);
    when(mockBeanDefinition.hasBeanClass()).thenReturn(true);
    when(mockBeanDefinition.getBeanClass()).thenReturn((Class) MapperFactoryBean.class);
    ResolvableType mockResolvableType = mock(ResolvableType.class);
    when(mockBeanDefinition.getResolvableType()).thenReturn(mockResolvableType);
    when(mockResolvableType.hasUnresolvableGenerics()).thenReturn(true);
    when(mockBeanDefinition.getPropertyValues()).thenReturn(new MutablePropertyValues());

    // when
    processor.resolveMapperFactoryBeanTypeIfNecessary(mockBeanDefinition);

    // then
    verify(mockBeanDefinition, never())
        .setConstructorArgumentValues(any(ConstructorArgumentValues.class));
    verify(mockBeanDefinition, never()).setTargetType(any(ResolvableType.class));
  }

  @Test
  void should_return_null_from_get_mapper_interface_when_bean_definition_is_null() {
    // when
    Class<?> result = processor.getMapperInterface(null);

    // then
    assertThat(result).isNull();
  }

  @Test
  void should_return_null_from_get_mapper_interface_when_property_values_throws_exception() {
    // given
    RootBeanDefinition mockBeanDefinition = mock(RootBeanDefinition.class);
    when(mockBeanDefinition.getPropertyValues()).thenThrow(new RuntimeException("Test exception"));

    // when
    Class<?> result = processor.getMapperInterface(mockBeanDefinition);

    // then
    assertThat(result).isNull();
  }

  @Test
  void should_return_mapper_interface_from_get_mapper_interface_when_property_exists() {
    // given
    RootBeanDefinition mockBeanDefinition = mock(RootBeanDefinition.class);
    MutablePropertyValues propertyValues = new MutablePropertyValues();
    propertyValues.addPropertyValue(new PropertyValue("mapperInterface", String.class));
    when(mockBeanDefinition.getPropertyValues()).thenReturn(propertyValues);

    // when
    Class<?> result = processor.getMapperInterface(mockBeanDefinition);

    // then
    assertThat(result).isEqualTo(String.class);
  }

  @Test
  void should_return_null_from_get_mapper_interface_when_property_does_not_exist() {
    // given
    RootBeanDefinition mockBeanDefinition = mock(RootBeanDefinition.class);
    MutablePropertyValues propertyValues = new MutablePropertyValues();
    when(mockBeanDefinition.getPropertyValues()).thenReturn(propertyValues);

    // when
    Class<?> result = processor.getMapperInterface(mockBeanDefinition);

    // then
    assertThat(result).isNull();
  }

}
