package cn.huava.sys.pojo.po;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 租户
 *
 * @author Camio1945
 */
@TableName("sys_tenant")
public class SysTenant implements Serializable {

  @TableId(type = IdType.INPUT)
  private Long id;

  /** 租户代码，6 位字符 */
  private String code;

  /** 租户名称 */
  private String name;

  /** 域名地址 */
  private String domain;

  /** 联系人 */
  private String contactName;

  /** 联系电话 */
  private String contactNumber;

  /** 过期时间 */
  private Date expireTime;

  /** 最大用户数量，<=0 表示不限 */
  private Integer maxUser;

  /** 备注 */
  private String remark;

  /** 删除标记，0 代表未删除，= id 代表已删除 */
  private Long deleteFlag;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getContactName() {
    return contactName;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  public String getContactNumber() {
    return contactNumber;
  }

  public void setContactNumber(String contactNumber) {
    this.contactNumber = contactNumber;
  }

  public Date getExpireTime() {
    return expireTime;
  }

  public void setExpireTime(Date expireTime) {
    this.expireTime = expireTime;
  }

  public Integer getMaxUser() {
    return maxUser;
  }

  public void setMaxUser(Integer maxUser) {
    this.maxUser = maxUser;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public Long getDeleteFlag() {
    return deleteFlag;
  }

  public void setDeleteFlag(Long deleteFlag) {
    this.deleteFlag = deleteFlag;
  }
}
