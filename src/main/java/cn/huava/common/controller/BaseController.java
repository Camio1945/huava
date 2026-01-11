package cn.huava.common.controller;

import cn.huava.common.pojo.po.BasePo;
import cn.huava.common.service.BaseService;
import cn.huava.common.validation.*;
import cn.hutool.v7.core.reflect.FieldUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

///
/// # Base controller that provides CRUD operations (excluding pagination queries)<br>
/// Generics: T - Entity type, M - MyBatis Mapper type, S - Service type <br>
///
/// @author Camio1945
@NullMarked
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public abstract class BaseController<S extends BaseService<M, T>, M extends BaseMapper<T>, T> {

  /**
   * Regarding @SuppressWarnings("java:S6813"): Without this annotation, SonarLint will warn that we
   * should not use the @Autowired annotation, but if we don't use the @Autowired annotation, then
   * each subclass would need to write code similar to this:
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

  /// Query object by id
  @GetMapping("/get/{id}")
  public ResponseEntity<T> getById(@PathVariable final Long id) {
    T entity = service.getById(id);
    if (entity instanceof BasePo basePo && basePo.getDeleteInfo() > 0) {
      return ResponseEntity.notFound().build();
    }
    afterGetById(entity);
    return ResponseEntity.ok(entity);
  }

  /**
   * Additional operations to perform after getting an object by id, subclasses can optionally
   * override this method when needed
   *
   * @param entity The object retrieved by id
   */
  protected void afterGetById(T entity) {}

  /** Create */
  @PostMapping("/create")
  @Transactional(rollbackFor = Throwable.class)
  public ResponseEntity<String> create(
      @RequestBody @Validated({Create.class}) final T entity) {
    Assert.isInstanceOf(BasePo.class, entity, "The entity must be an instance of BasePo");
    BasePo.beforeCreate(entity);
    beforeSave(entity);
    boolean success = service.save(entity);
    Assert.isTrue(success, "Failed to create entity");
    afterSave(entity);
    Long id = ((BasePo) entity).getId();
    return ResponseEntity.ok(id.toString());
  }

  /**
   * Additional operations to perform before saving to the database, subclasses can optionally
   * override this method when needed
   *
   * @param entity The object to be saved
   */
  protected void beforeSave(T entity) {}

  /**
   * Additional operations to perform after saving to the database, subclasses can optionally
   * override this method when needed
   *
   * @param entity The object after saving
   */
  protected void afterSave(T entity) {}

  /** Update */
  @PutMapping("/update")
  @Transactional(rollbackFor = Throwable.class)
  public ResponseEntity<Void> update(
      @RequestBody @Validated({Update.class}) final T entity) {
    BasePo.beforeUpdate(entity);
    beforeUpdate(entity);
    boolean success = service.updateById(entity);
    Assert.isTrue(success, "Failed to update entity");
    afterUpdate(entity);
    return ResponseEntity.ok(null);
  }

  /**
   * Additional operations to perform before updating to the database, subclasses can optionally
   * override this method when needed
   *
   * @param entity The object to be updated
   */
  protected void beforeUpdate(T entity) {}

  /**
   * Additional operations to perform after updating to the database, subclasses can optionally
   * override this method when needed
   *
   * @param entity The object after updating
   */
  protected void afterUpdate(T entity) {}

  /** Delete */
  @DeleteMapping("/delete")
  public ResponseEntity<Void> delete(
      @RequestBody @Validated({Delete.class}) final T entity) {
    Long id = (Long) FieldUtil.getFieldValue(entity, "id");
    Object obj = beforeDelete(id);
    boolean success = service.softDelete(id);
    Assert.isTrue(success, "Failed to delete entity");
    afterDelete(obj);
    return ResponseEntity.ok(null);
  }

  /**
   * Additional operations to perform before deleting data, subclasses can optionally override this
   * method when needed
   *
   * @param id The id of the object to be deleted
   * @return The object to return is determined by the subclass, this returned object will be passed
   *     to the {@link #afterDelete(Object)} method
   */
  protected @Nullable Object beforeDelete(Long id) {
    return null;
  }

  /**
   * Additional operations to perform after deleting data, subclasses can optionally override this
   * method when needed
   *
   * @param obj The object returned by the {@link #beforeDelete} method
   */
  protected void afterDelete(@Nullable Object obj) {}
}
