package cn.huava.sys.pojo.dto;

import cn.huava.sys.pojo.po.PermPo;
import java.util.List;
import lombok.Data;

/**
 * @author Camio1945
 */
@Data
public class PermDto extends PermPo {
  private List<PermDto> children;
}
