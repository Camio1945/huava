package cn.huava.common.controller;

import cn.huava.common.pojo.po.BasePo;
import cn.huava.common.service.BaseService;
import cn.huava.common.validation.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.NonNull;
import org.dromara.hutool.core.reflect.FieldUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
   * public SysUserController(SysUserService service) {
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
    return ResponseEntity.ok(entity);
  }

  @PostMapping("/create")
  public ResponseEntity<Long> create(
      @RequestBody @NonNull @Validated({Create.class}) final T entity) {
    Assert.isInstanceOf(BasePo.class, entity, "The entity must be an instance of BasePo");
    BasePo.beforeCreate(entity);
    boolean success = service.save(entity);
    Assert.isTrue(success, "Failed to create entity");
    Long id = ((BasePo) entity).getId();
    return ResponseEntity.ok(id);
  }

  @PutMapping("/update")
  public ResponseEntity<Void> update(
      @RequestBody @NonNull @Validated({Update.class}) final T entity) {
    BasePo.beforeUpdate(entity);
    boolean success = service.updateById(entity);
    Assert.isTrue(success, "Failed to update entity");
    return ResponseEntity.ok(null);
  }

  @PatchMapping("/patch")
  public ResponseEntity<Void> patch(
      @RequestBody final T entity, @RequestParam(required = false) final String... fields) {
    // TODO The patch method is not implemented yet
    Assert.isTrue(false, "The patch method is not implemented yet");
    return ResponseEntity.ok(null);
  }

  @DeleteMapping("/delete")
  public ResponseEntity<Void> delete(
      @RequestBody @NonNull @Validated({Delete.class}) final T entity) {
    Long id = (Long) FieldUtil.getFieldValue(entity, "id");
    boolean success = service.softDelete(id);
    Assert.isTrue(success, "Failed to delete entity");
    return ResponseEntity.ok(null);
  }
}
