package cn.huava.common.util;

import cn.hutool.v7.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Lqw;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.NonNull;

/**
 * @author Camio1945
 */
public class MybatisPlusTool {
  private MybatisPlusTool() {}

  public static <T> LambdaQueryWrapper<T> undeletedWrapper(
      @NonNull final SFunction<T, ?> deleteInfoColumn) {
    Assert.equals("getDeleteInfo", LambdaUtils.extract(deleteInfoColumn).getImplMethodName());
    return new Lqw<T>().eq(deleteInfoColumn, 0);
  }
}
