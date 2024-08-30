package cn.huava.sys.pojo.qo;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

/**
 * @author Camio1945
 */
@Data
public class SetPermQo {
  @NotNull(message = "角色ID不能为空")
  private Long roleId;

  private List<Long> permIds;
}
