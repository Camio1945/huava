package cn.huava.common.util;

import static cn.huava.common.constant.CommonConstant.MAX_PAGE_SIZE;

import cn.huava.common.pojo.qo.PageQo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.NonNull;
import org.dromara.hutool.core.lang.Assert;

/**
 * @author Camio1945
 */
public class MybatisPlusUtil {
  private MybatisPlusUtil() {}

  protected static <T> Page<T> buildPage(PageQo pageQo) {
    Page<T> page = new Page<>();
    int current = pageQo.getCurrentPage();
    current = current <= 0 ? 1 : current;
    page.setCurrent(current);
    int size = pageQo.getSize();
    size = size <= 0 ? 10 : size;
    size = Math.min(size, MAX_PAGE_SIZE);
    page.setSize(size);
    return page;
  }

  protected static <T> LambdaQueryWrapper<T> buildUndeletedWrapper(
      @NonNull final SFunction<T, ?> deleteInfoColumn) {
    Assert.equals("getDeleteInfo", LambdaUtils.extract(deleteInfoColumn).getImplMethodName());
    return new LambdaQueryWrapper<T>().eq(deleteInfoColumn, 0);
  }
}
