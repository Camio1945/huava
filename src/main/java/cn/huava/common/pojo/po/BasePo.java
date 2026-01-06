package cn.huava.common.pojo.po;

import static cn.huava.common.constant.CommonConstant.ADMIN_USER_ID;

import cn.huava.common.util.Fn;
import cn.huava.sys.pojo.po.UserPo;
import cn.hutool.v7.core.data.id.IdUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serial;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

///
/// # Base PO class
///
/// @author Camio1945
@Slf4j
@Data
public class BasePo implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  /** Primary key */
  @TableId private Long id;

  /** Creator ID */
  private Long createdBy;

  /** Creation time */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date createdAt;

  /** Updater ID */
  private Long updatedBy;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date updatedAt;

  /** Deletion info: 0=not deleted, other values=deletion time */
  private Long deleteInfo;

  public static <T> void beforeCreate(@NonNull T entity) {
    if (entity instanceof BasePo basePo) {
      Date date = new Date();
      basePo.setId(IdUtil.getSnowflakeNextId());
      Long userId = getLoginUserId();
      basePo.setCreatedBy(userId);
      basePo.setCreatedAt(date);
      basePo.setUpdatedBy(userId);
      basePo.setUpdatedAt(date);
      basePo.setDeleteInfo(0L);
    }
  }

  private static Long getLoginUserId() {
    UserPo loginUser;
    try {
      loginUser = Fn.getLoginUser();
      return loginUser.getId();
    } catch (Exception e) {
      log.warn("Failed to get login user: {}", e.getMessage());
      return ADMIN_USER_ID;
    }
  }

  public static <T> void beforeUpdate(@NonNull T entity) {
    if (entity instanceof BasePo basePo) {
      basePo.setUpdatedBy(getLoginUserId());
      basePo.setUpdatedAt(new Date());
    }
  }

  public static <T> void beforeDelete(@NonNull T entity) {
    if (entity instanceof BasePo basePo) {
      Date now = new Date();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
      String timeStr = sdf.format(now);
      basePo.setUpdatedBy(getLoginUserId());
      basePo.setUpdatedAt(now);
      basePo.setDeleteInfo(Long.parseLong(timeStr));
    }
  }
}
