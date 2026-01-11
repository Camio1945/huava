package cn.huava.common.pojo.po;

import static org.assertj.core.api.Assertions.*;

import java.util.Date;
import org.junit.jupiter.api.Test;

/**
 * @author Camio1945
 */
class BasePoTest {
  @Test
  void should_perform_full_lifecycle_operations() {
    BasePo basePo = new BasePo();
    basePo.setId(1L);
    basePo.setCreatedBy(1L);
    basePo.setCreatedAt(new Date());
    basePo.setUpdatedBy(1L);
    basePo.setUpdatedAt(new Date());
    basePo.setDeleteInfo(0L);

    assertThat(basePo.getId()).isEqualTo(1L);
    assertThat(basePo.getCreatedBy()).isEqualTo(1L);
    assertThat(basePo.getCreatedAt()).isNotNull();
    assertThat(basePo.getUpdatedBy()).isEqualTo(1L);
    assertThat(basePo.getUpdatedAt()).isNotNull();
    assertThat(basePo.getDeleteInfo()).isZero();

    BasePo.beforeCreate(basePo);
    assertThat(basePo.getCreatedAt()).isNotNull();
    assertThat(basePo.getUpdatedAt()).isNotNull();
    assertThat(basePo.getDeleteInfo()).isZero();

    BasePo.beforeUpdate(basePo);
    assertThat(basePo.getUpdatedAt()).isNotNull();

    BasePo.beforeDelete(basePo);
    assertThat(basePo.getDeleteInfo()).isNotNull();
  }

  @Test
  void should_not_process_non_BasePo_entities_in_beforeCreate() {
    // Test the generic method with a non-BasePo entity to cover the instanceof condition
    Object nonBasePoEntity = new Object();
    // Calling beforeCreate with a non-BasePo entity should not throw an exception
    // and should not affect the non-BasePo entity
    assertThatCode(() -> BasePo.beforeCreate(nonBasePoEntity)).doesNotThrowAnyException();
    // The method should complete without processing the non-BasePo entity
  }

  @Test
  void should_not_process_non_BasePo_entities_in_beforeUpdate() {
    // Test the generic method with a non-BasePo entity to cover the instanceof condition
    Object nonBasePoEntity = new Object();
    // Calling beforeUpdate with a non-BasePo entity should not throw an exception
    // and should not affect the non-BasePo entity
    assertThatCode(() -> BasePo.beforeUpdate(nonBasePoEntity)).doesNotThrowAnyException();
    // The method should complete without processing the non-BasePo entity
  }

  @Test
  void should_not_process_non_BasePo_entities_in_beforeDelete() {
    // Test the generic method with a non-BasePo entity to cover the instanceof condition
    Object nonBasePoEntity = new Object();
    // Calling beforeDelete with a non-BasePo entity should not throw an exception
    // and should not affect the non-BasePo entity
    assertThatCode(() -> BasePo.beforeDelete(nonBasePoEntity)).doesNotThrowAnyException();
    // The method should complete without processing the non-BasePo entity
  }
}
