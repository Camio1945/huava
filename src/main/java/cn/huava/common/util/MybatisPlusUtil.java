package cn.huava.common.util;

import cn.hutool.v7.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.NonNull;

/**
 * @author Camio1945
 */
class MybatisPlusUtil {
  private MybatisPlusUtil() {}

  protected static <T> LambdaQueryWrapper<T> buildUndeletedWrapper(
      @NonNull final SFunction<T, ?> deleteInfoColumn) {
    Assert.equals("getDeleteInfo", LambdaUtils.extract(deleteInfoColumn).getImplMethodName());
    return new LambdaQueryWrapper<T>().eq(deleteInfoColumn, 0);
  }
}
