package cn.huava.common.service;

import cn.huava.common.pojo.po.BasePo;
import cn.hutool.v7.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jspecify.annotations.NonNull;

///
/// # Base Service to provide common CRUD operations. <br>
/// Generics: T - Entity type, M - MyBatis Mapper type <br>
///
/// @author Camio1945
public abstract class BaseService<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

  /**
   * soft delete entity by id, set delete_info to current time
   *
   * @param id entity id
   * @return true if success, false otherwise
   */
  public boolean softDelete(@NonNull Long id) {
    T entity = baseMapper.selectById(id);
    if (entity == null) {
      return false;
    }
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
