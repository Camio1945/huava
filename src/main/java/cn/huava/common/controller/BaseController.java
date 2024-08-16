package cn.huava.common.controller;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
public abstract class BaseController<S extends ServiceImpl<M, T>, M extends BaseMapper<T>, T> {

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
  public ResponseEntity<T> getById(@PathVariable Long id) {
    T entity = service.getById(id);
    return ResponseEntity.ok(entity);
  }

  @PostMapping("/create")
  public ResponseEntity<T> create(@RequestBody T entity) {
    boolean success = service.save(entity);
    Assert.isTrue(success, "Failed to create entity");
    return ResponseEntity.status(HttpStatus.CREATED).body(entity);
  }

  @PutMapping("/update")
  public ResponseEntity<T> update(@RequestBody T entity) {
    boolean success = service.updateById(entity);
    Assert.isTrue(success, "Failed to update entity");
    return ResponseEntity.ok(entity);
  }

  @PatchMapping("/patch")
  public ResponseEntity<T> patch(
      @RequestBody T entity, @RequestParam(required = false) String... fields) {
    // TODO The patch method is not implemented yet
    Assert.isTrue(false, "The patch method is not implemented yet");
    return ResponseEntity.ok(entity);
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    boolean success = service.removeById(id);
    Assert.isTrue(success, "Failed to delete entity");
    return ResponseEntity.noContent().build();
  }
}
