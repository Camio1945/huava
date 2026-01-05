package cn.huava.common.pojo.po;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;
import org.junit.jupiter.api.Test;

/**
 * @author Camio1945
 */
class BasePoTest {
  @Test
  void testBasePo() {
    BasePo basePo = new BasePo();
    basePo.setId(1L);
    basePo.setCreatedBy(1L);
    basePo.setCreatedAt(new Date());
    basePo.setUpdatedBy(1L);
    basePo.setUpdatedAt(new Date());
    basePo.setDeleteInfo(0L);

    assertEquals(1L, basePo.getId());
    assertEquals(1L, basePo.getCreatedBy());
    assertNotNull(basePo.getCreatedAt());
    assertEquals(1L, basePo.getUpdatedBy());
    assertNotNull(basePo.getUpdatedAt());
    assertEquals(0L, basePo.getDeleteInfo());

    BasePo.beforeCreate(basePo);
    assertNotNull(basePo.getCreatedAt());
    assertNotNull(basePo.getUpdatedAt());
    assertEquals(0L, basePo.getDeleteInfo());

    BasePo.beforeUpdate(basePo);
    assertNotNull(basePo.getUpdatedAt());

    BasePo.beforeDelete(basePo);
    assertNotNull(basePo.getDeleteInfo());
  }
}