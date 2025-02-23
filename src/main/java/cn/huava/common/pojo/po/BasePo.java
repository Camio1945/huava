package cn.huava.common.pojo.po;

import static org.dromara.hutool.core.date.DateFormatPool.PURE_DATETIME_MS_FORMAT;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.NonNull;

/**
 * PO 基类
 *
 * @author Camio1945
 */
@Data
public class BasePo implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  /** 主键 */
  @TableId private Long id;

  /** 创建时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date updateTime;

  /** 删除信息：0-未删除，其他值-删除时间 */
  private Long deleteInfo;

  public static <T> void beforeCreate(@NonNull T entity) {
    if (entity instanceof BasePo basePo) {
      Date date = new Date();
      basePo.setId(null).setCreateTime(date).setUpdateTime(date).setDeleteInfo(0L);
    }
  }

  public static <T> void beforeUpdate(@NonNull T entity) {
    if (entity instanceof BasePo basePo) {
      basePo.setUpdateTime(new Date());
    }
  }

  public static <T> void beforeDelete(@NonNull T entity) {
    if (entity instanceof BasePo basePo) {
      Date now = new Date();
      String timeStr = PURE_DATETIME_MS_FORMAT.format(now);
      basePo.setDeleteInfo(Long.parseLong(timeStr));
    }
  }
}
