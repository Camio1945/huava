package cn.huava.common.controller;

import cn.huava.common.pojo.dto.ApiResponseDataDto;
import cn.huava.common.pojo.po.BasePo;
import cn.huava.common.service.BaseService;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
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
  public ResponseEntity<ApiResponseDataDto<T>> getById(@PathVariable @NonNull Long id) {
    T entity = service.getById(id);
    if (entity instanceof BasePo basePo && basePo.getDeleteInfo() > 0) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(new ApiResponseDataDto<>(entity));
  }

  @PostMapping("/create")
  public ResponseEntity<ApiResponseDataDto<Long>> create(@RequestBody @NonNull T entity) {
    Assert.isInstanceOf(BasePo.class, entity, "The entity must be an instance of BasePo");
    BasePo.beforeCreate(entity);
    boolean success = service.save(entity);
    Assert.isTrue(success, "Failed to create entity");
    Long id = ((BasePo) entity).getId();
    return ResponseEntity.ok(new ApiResponseDataDto<>(id));
  }

  @PutMapping("/update")
  public ResponseEntity<ApiResponseDataDto<Void>> update(@RequestBody @NonNull T entity) {
    BasePo.beforeUpdate(entity);
    boolean success = service.updateById(entity);
    Assert.isTrue(success, "Failed to update entity");
    return ResponseEntity.ok(new ApiResponseDataDto<>());
  }

  @PatchMapping("/patch")
  public ResponseEntity<ApiResponseDataDto<Void>> patch(
      @RequestBody T entity, @RequestParam(required = false) String... fields) {
    // TODO The patch method is not implemented yet
    Assert.isTrue(false, "The patch method is not implemented yet");
    return ResponseEntity.ok(new ApiResponseDataDto<>());
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<ApiResponseDataDto<Void>> delete(@PathVariable @NonNull Long id) {
    boolean success = service.softDelete(id);
    Assert.isTrue(success, "Failed to delete entity");
    return ResponseEntity.ok(new ApiResponseDataDto<>());
  }
}
