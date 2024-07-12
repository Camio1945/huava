package cn.huava.sys.service;

import cn.huava.sys.pojo.po.SysTenant;

/**
 * @author Camio1945
 */
public interface ISysTenantService {

  /**
   * 根据 id 获取租户信息
   *
   * @param id 租户 id
   * @return 租户信息
   */
  SysTenant getById(Long id);

  /**
   * 新增租户
   *
   * @param sysTenant 租户信息
   * @return true 表示新增成功，false 表示新增失败
   */
  boolean add(SysTenant sysTenant);

  /**
   * 更新租户信息
   *
   * @param sysTenant 租户信息
   * @return true 表示更新成功，false 表示更新失败
   */
  boolean update(SysTenant sysTenant);

  /**
   * 根据 id 删除租户信息
   *
   * @param id 租户 id
   * @return true 表示删除成功，false 表示删除失败
   */
  boolean delete(Long id);
}
