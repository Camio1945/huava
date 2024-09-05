package cn.huava.common.pojo.qo;

import static cn.huava.common.constant.CommonConstant.MAX_PAGE_SIZE;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import org.dromara.hutool.core.lang.Assert;

/**
 * 分页查询对象
 *
 * @author Camio1945
 */
@Data
public class PageQo<T> extends Page<T> {
  @Override
  public Page<T> setSize(long size) {
    Assert.isTrue(size > 0 && size <= MAX_PAGE_SIZE, "每页条数必须大于 0 且小于等于 " + MAX_PAGE_SIZE);
    return super.setSize(size);
  }

  @Override
  public Page<T> setCurrent(long current) {
    Assert.isTrue(current > 0, "当前页数必须大于 0");
    return super.setCurrent(current);
  }
}
