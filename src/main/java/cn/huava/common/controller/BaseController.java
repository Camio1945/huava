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
 * Base Controller to provide common CRUD operations. <br>
 * Generic: T - Entity Type, M - Mapper Type, S - Service Type <br>
 *
 * @author Camio1945
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public abstract class BaseController<S extends BaseService<M, T>, M extends BaseMapper<T>, T> {

  /**
   * About @SuppressWarnings("java:S6813"): Without @SuppressWarnings("java:S6813"), the SonarLint
   * will complain about the @Autowired annotation. But without @Autowired annotation, every
   * subclass of this class will need to write something like the following code:
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

  @GetMapping("/get/{id}")
  public ResponseEntity<T> getById(@PathVariable @NonNull final Long id) {
    T entity = service.getById(id);
    if (entity instanceof BasePo basePo && basePo.getDeleteInfo() > 0) {
      return ResponseEntity.notFound().build();
    }
    afterGetById(entity);
    return ResponseEntity.ok(entity);
  }

  /** Intended to be overridden by subclass if needed. */
  protected void afterGetById(T entity) {}

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

  /** Intended to be overridden by subclass if needed. */
  @SuppressWarnings("unused")
  protected void beforeSave(@NonNull T entity) {}

  /** Intended to be overridden by subclass if needed. */
  @SuppressWarnings("unused")
  protected void afterSave(@NonNull T entity) {}

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

  /** Intended to be overridden by subclass if needed. */
  @SuppressWarnings("unused")
  protected void beforeUpdate(@NonNull T entity) {}

  /** Intended to be overridden by subclass if needed. */
  @SuppressWarnings("unused")
  protected void afterUpdate(@NonNull T entity) {}

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

  /** Intended to be overridden by subclass if needed. */
  @SuppressWarnings("unused")
  protected Object beforeDelete(@NonNull Long id) {
    return null;
  }

  /** Intended to be overridden by subclass if needed. */
  @SuppressWarnings("unused")
  protected void afterDelete(Object obj) {}
}
