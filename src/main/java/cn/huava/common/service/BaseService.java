package cn.huava.common.service;

import cn.huava.common.pojo.po.BasePo;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import org.dromara.hutool.core.lang.Assert;

/**
 * Base Service to provide common CRUD operations. <br>
 * Generic: T - Entity Type, M - Mapper Type <br>
 *
 * @author Camio1945
 */
public abstract class BaseService<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

  /**
   * soft delete entity by id, set delete_info to current time
   *
   * @param id entity id
   * @return true if success, false otherwise
   */
  public boolean softDelete(@NonNull Long id) {
    T entity = baseMapper.selectById(id);
    Assert.isTrue(entity instanceof BasePo, "Entity must be instance of BasePo");
    BasePo basePo = (BasePo) entity;
    if (basePo.getDeleteInfo() == 0) {
      BasePo.beforeDelete(entity);
      baseMapper.update(
          null, new UpdateWrapper<T>().eq("id", id).set("delete_info", basePo.getDeleteInfo()));
    }
    return true;
  }
}
