package cn.huava.common.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

import cn.huava.common.pojo.po.BasePo;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BaseServiceTest {

  @Mock private BaseMapper<TestEntity> mockMapper;

  private TestBaseService baseService;

  @BeforeEach
  void setUp() {
    baseService = new TestBaseService();
    baseService.setBaseMapper(mockMapper);
  }

  @Test
  void should_return_false_when_entity_not_found_by_id() {
    // Given
    Long id = 1L;
    when(mockMapper.selectById(id)).thenReturn(null);

    // When
    boolean result = baseService.softDelete(id);

    // Then
    assertThat(result).isFalse();
    verify(mockMapper).selectById(id);
  }

  @Test
  void should_return_true_when_entity_is_already_soft_deleted() {
    // Given
    Long id = 1L;
    TestEntity entity = new TestEntity();
    entity.setDeleteInfo(123456789L); // Already soft deleted
    when(mockMapper.selectById(id)).thenReturn(entity);

    // When
    boolean result = baseService.softDelete(id);

    // Then
    assertThat(result).isTrue();
    verify(mockMapper).selectById(id);
    verify(mockMapper, never()).update(any(), any(UpdateWrapper.class));
  }

  @Test
  void should_perform_soft_delete_when_entity_exists_and_not_deleted() {
    // Given
    Long id = 1L;
    TestEntity entity = new TestEntity();
    entity.setDeleteInfo(0L); // Not deleted
    when(mockMapper.selectById(id)).thenReturn(entity);

    // When
    boolean result = baseService.softDelete(id);

    // Then
    assertThat(result).isTrue();
    verify(mockMapper).selectById(id);
    verify(mockMapper).update(isNull(), any(UpdateWrapper.class));
  }

  // Concrete implementation for testing
  private static class TestBaseService extends BaseService<BaseMapper<TestEntity>, TestEntity> {
    public void setBaseMapper(BaseMapper<TestEntity> mapper) {
      this.baseMapper = mapper;
    }
  }

  // Test entity that extends BasePo
  @Data
  private static class TestEntity extends BasePo {
    private String name;
  }
}
