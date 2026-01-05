package cn.huava.common.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import cn.huava.common.pojo.po.BasePo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.Test;

class MybatisPlusToolTest {

  @Test
  void testUndeletedWrapper() {
    LambdaQueryWrapper<BasePo> wrapper = MybatisPlusTool.undeletedWrapper(BasePo::getDeleteInfo);
    assertNotNull(wrapper);
    // You might want to further inspect the wrapper, but this at least ensures it doesn't throw
    // an exception and returns a non-null object.
  }

  @Test
  void testUndeletedWrapperWithInvalidColumn() {
    // Test with a column that is not 'getDeleteInfo'
    assertThrows(
        IllegalArgumentException.class,
        () -> MybatisPlusTool.undeletedWrapper(BasePo::getId));
  }
}
