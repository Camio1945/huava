package cn.huava.common.pojo.dto;

import java.util.List;
import lombok.Data;

/**
 * @author Camio1945
 */
@Data
public class PageDto<T> {
  private List<T> list;
  private long count;

  public PageDto() {}

  public PageDto(List<T> list, long count) {
    this.list = list;
    this.count = count;
  }
}
