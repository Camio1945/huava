package cn.huava.common.pojo.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * PO 基类
 *
 * @author ruoyi
 */
@Data
public class BasePo implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  /** 搜索值 */
  @JsonIgnore private transient String searchValue;

  /** 创建者 */
  private String createBy;

  /** 创建时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;

  /** 更新者 */
  private String updateBy;

  /** 更新时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date updateTime;

  /** 备注 */
  private String remark;

  /** 请求参数 */
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private transient Map<String, Object> params;

  public Map<String, Object> getParams() {
    if (params == null) {
      params = HashMap.newHashMap(0);
    }
    return params;
  }
}
