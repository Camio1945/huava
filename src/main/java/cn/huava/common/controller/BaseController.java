package cn.huava.common.controller;

import cn.huava.common.pojo.po.BasePo;
import cn.huava.common.service.BaseService;
import cn.huava.common.validation.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.NonNull;
import org.dromara.hutool.core.reflect.FieldUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 基础控制器，提供增删改查的功能（不包含分页查询）<br>
 * 泛型：T - 实体类型， M - MyBatis 的 Mapper 类型， S - Service 类型 <br>
 *
 * @author Camio1945
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public abstract class BaseController<S extends BaseService<M, T>, M extends BaseMapper<T>, T> {

  /**
   * 关于 @SuppressWarnings("java:S6813")： 如果没有这个注解， SonarLint 会警告说我们不应该使用 @Autowired
   * 注解，但是如果不使用 @Autowired 注解，那每一个子类都需要写一些类似这样的代码:
   *
   * <pre>
   * public UserController(UserService service) {
   *   super(service);
   * }
   * </pre>
   */
  @SuppressWarnings("java:S6813")
  @Autowired
  protected S service;

  /** 根据 id 查询对象 */
  @GetMapping("/get/{id}")
  public ResponseEntity<T> getById(@PathVariable @NonNull final Long id) {
    T entity = service.getById(id);
    if (entity instanceof BasePo basePo && basePo.getDeleteInfo() > 0) {
      return ResponseEntity.notFound().build();
    }
    afterGetById(entity);
    return ResponseEntity.ok(entity);
  }

  /** 增 */
  @PostMapping("/create")
  @Transactional(rollbackFor = Throwable.class)
  public ResponseEntity<String> create(
      @RequestBody @NonNull @Validated({Create.class}) final T entity) {
    Assert.isInstanceOf(BasePo.class, entity, "The entity must be an instance of BasePo");
    BasePo.beforeCreate(entity);
    beforeSave(entity);
    boolean success = service.save(entity);
    Assert.isTrue(success, "Failed to create entity");
    afterSave(entity);
    Long id = ((BasePo) entity).getId();
    return ResponseEntity.ok(id.toString());
  }

  /** 改 */
  @PutMapping("/update")
  @Transactional(rollbackFor = Throwable.class)
  public ResponseEntity<Void> update(
      @RequestBody @NonNull @Validated({Update.class}) final T entity) {
    BasePo.beforeUpdate(entity);
    beforeUpdate(entity);
    boolean success = service.updateById(entity);
    Assert.isTrue(success, "Failed to update entity");
    afterUpdate(entity);
    return ResponseEntity.ok(null);
  }

  /** 删 */
  @DeleteMapping("/delete")
  public ResponseEntity<Void> delete(
      @RequestBody @NonNull @Validated({Delete.class}) final T entity) {
    Long id = (Long) FieldUtil.getFieldValue(entity, "id");
    Object obj = beforeDelete(id);
    boolean success = service.softDelete(id);
    Assert.isTrue(success, "Failed to delete entity");
    afterDelete(obj);
    return ResponseEntity.ok(null);
  }

  /**
   * 根据 id 获取对象之后额外要做的事情，子类可在有需要的时候选择性地覆盖本方法
   *
   * @param entity 根据 id 查询到的对象
   */
  protected void afterGetById(T entity) {}

  /**
   * 在保存到数据库之前额外要做的事情，子类可在有需要的时候选择性地覆盖本方法
   *
   * @param entity 要保存的对象
   */
  protected void beforeSave(@NonNull T entity) {}

  /**
   * 在保存到数据库之后额外要做的事情，子类可在有需要的时候选择性地覆盖本方法
   *
   * @param entity 保存后的对象
   */
  protected void afterSave(@NonNull T entity) {}

  /**
   * 在更新到数据库之前额外要做的事情，子类可在有需要的时候选择性地覆盖本方法
   *
   * @param entity 要更新的对象
   */
  protected void beforeUpdate(@NonNull T entity) {}

  /**
   * 在更新到数据库之后额外要做的事情，子类可在有需要的时候选择性地覆盖本方法
   *
   * @param entity 更新后的对象
   */
  protected void afterUpdate(@NonNull T entity) {}

  /**
   * 在删除数据之前额外要做的事情，子类可在有需要的时候选择性地覆盖本方法
   *
   * @param id 要删除的对象的 id
   * @return 由子类决定要返回什么对象，这个返回的对象会传给 {@link #afterDelete(Object)} 方法
   */
  protected Object beforeDelete(@NonNull Long id) {
    return null;
  }

  /**
   * 在删除数据之后额外要做的事情，子类可在有需要的时候选择性地覆盖本方法
   *
   * @param obj 是 {@link #beforeDelete} 方法返回的对象
   */
  protected void afterDelete(Object obj) {}
}
