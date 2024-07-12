package cn.huava.sys.service.impl;

import cn.huava.sys.mapper.SysTenantMapper;
import cn.huava.sys.pojo.po.SysTenant;
import cn.huava.sys.service.ISysTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Camio1945
 */
@Service
public class SysTenantServiceImpl implements ISysTenantService {
  @Autowired private SysTenantMapper sysTenantMapper;

  @Override
  public SysTenant getById(Long id) {
    return sysTenantMapper.selectById(id);
  }

  @Override
  public boolean add(SysTenant sysTenant) {
    return sysTenantMapper.insert(sysTenant) > 0;
  }

  @Override
  public boolean update(SysTenant sysTenant) {
    return sysTenantMapper.updateById(sysTenant) > 0;
  }

  @Override
  public boolean delete(Long id) {
    return sysTenantMapper.deleteById(id) > 0;
  }
}
