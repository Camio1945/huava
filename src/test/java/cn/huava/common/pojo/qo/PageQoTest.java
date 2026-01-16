package cn.huava.common.pojo.qo;

import static cn.huava.common.constant.CommonConstant.MAX_PAGE_SIZE;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link PageQo}
 *
 * @author Qwen
 */
class PageQoTest {

  @Test
  void should_create_instance_successfully() {
    // Given
    PageQo<String> pageQo = new PageQo<>();

    // Then
    assertThat(pageQo).isNotNull();
  }

  @Test
  void should_set_size_successfully_when_valid_size() {
    // Given
    PageQo<String> pageQo = new PageQo<>();
    long validSize = 10L;

    // When
    PageQo<String> result = (PageQo<String>) pageQo.setSize(validSize);

    // Then
    assertThat(result).isEqualTo(pageQo);
    assertThat(pageQo.getSize()).isEqualTo(validSize);
  }

  @Test
  void should_throw_exception_when_size_is_zero() {
    // Given
    PageQo<String> pageQo = new PageQo<>();
    long invalidSize = 0L;

    // When & Then
    assertThatThrownBy(() -> pageQo.setSize(invalidSize))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("每页条数必须大于 0 且小于等于 " + MAX_PAGE_SIZE);
  }

  @Test
  void should_throw_exception_when_size_is_negative() {
    // Given
    PageQo<String> pageQo = new PageQo<>();
    long invalidSize = -5L;

    // When & Then
    assertThatThrownBy(() -> pageQo.setSize(invalidSize))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("每页条数必须大于 0 且小于等于 " + MAX_PAGE_SIZE);
  }

  @Test
  void should_throw_exception_when_size_exceeds_max_limit() {
    // Given
    PageQo<String> pageQo = new PageQo<>();
    long invalidSize = MAX_PAGE_SIZE + 1;

    // When & Then
    assertThatThrownBy(() -> pageQo.setSize(invalidSize))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("每页条数必须大于 0 且小于等于 " + MAX_PAGE_SIZE);
  }

  @Test
  void should_set_current_successfully_when_valid_current() {
    // Given
    PageQo<String> pageQo = new PageQo<>();
    long validCurrent = 2L;

    // When
    PageQo<String> result = (PageQo<String>) pageQo.setCurrent(validCurrent);

    // Then
    assertThat(result).isEqualTo(pageQo);
    assertThat(pageQo.getCurrent()).isEqualTo(validCurrent);
  }

  @Test
  void should_throw_exception_when_current_is_zero() {
    // Given
    PageQo<String> pageQo = new PageQo<>();
    long invalidCurrent = 0L;

    // When & Then
    assertThatThrownBy(() -> pageQo.setCurrent(invalidCurrent))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("当前页数必须大于 0");
  }

  @Test
  void should_throw_exception_when_current_is_negative() {
    // Given
    PageQo<String> pageQo = new PageQo<>();
    long invalidCurrent = -3L;

    // When & Then
    assertThatThrownBy(() -> pageQo.setCurrent(invalidCurrent))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("当前页数必须大于 0");
  }

  @Test
  void should_inherit_Page_properties_and_methods() {
    // Given
    PageQo<String> pageQo = new PageQo<>();
    long size = 5L;
    long current = 2L;

    // When
    pageQo.setSize(size);
    pageQo.setCurrent(current);

    // Then
    assertThat(pageQo.getSize()).isEqualTo(size);
    assertThat(pageQo.getCurrent()).isEqualTo(current);
    assertThat(pageQo.getTotal()).isZero(); // Default value
    assertThat(pageQo.getRecords()).isEmpty(); // Default value
  }
}
