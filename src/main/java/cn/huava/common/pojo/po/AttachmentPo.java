package cn.huava.common.pojo.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * attachment 附件
 *
 * @author Camio1945
 */
@Data
@TableName("common_attachment")
public class AttachmentPo implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  /** 主键 */
  @TableId private Long id;

  /** 地址（可以是相对路径，也可以是绝对路径，但是要保持统一，要么都是相对路径，要么都是绝对路径） */
  private String url;

  /** 原文件名称 */
  private String originalName;

  /** 文件大小（字节） */
  private Long size;

  /** 文件大小（适合人类阅读习惯） */
  private String humanSize;

  /** 创建时间 */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;
}
