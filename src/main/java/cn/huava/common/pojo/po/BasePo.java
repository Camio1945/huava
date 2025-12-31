package cn.huava.common.pojo.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serial;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import org.springframework.lang.NonNull;

/**
 * Base PO class
 *
 * @author Camio1945
 */
public class BasePo implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  /** Primary key */
  @TableId private Long id;

  /** Creation time */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date updateTime;

  /** Deletion info: 0-not deleted, other values-deletion time */
  private Long deleteInfo;

  public static <T> void beforeCreate(@NonNull T entity) {
    if (entity instanceof BasePo basePo) {
      Date date = new Date();
      basePo.setId(null);
      basePo.setCreateTime(date);
      basePo.setUpdateTime(date);
      basePo.setDeleteInfo(0L);
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
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
      String timeStr = sdf.format(now);
      basePo.setDeleteInfo(Long.parseLong(timeStr));
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public Date getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
  }

  public Long getDeleteInfo() {
    return deleteInfo;
  }

  public void setDeleteInfo(Long deleteInfo) {
    this.deleteInfo = deleteInfo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BasePo basePo = (BasePo) o;
    return Objects.equals(id, basePo.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
