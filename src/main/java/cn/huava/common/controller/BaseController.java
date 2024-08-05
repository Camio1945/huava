package cn.huava.common.controller;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Base Controller to provide common CRUD operations. <br>
 *
 * @author Camio1945
 */
public abstract class BaseController<S extends ServiceImpl<M, T>, M extends BaseMapper<T>, T> {

  /**
   * The SonarLint will complain about the @Autowired annotation, <br>
   * but if without it, every subclass of this class will need to write <br>
   * something like the following code:<br>
   *
   * <pre>
   * public SysUserController(SysUserService service) {
   *   super(service);
   * }
   * </pre>
   */
  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  protected S service;

  @GetMapping("/{id}")
  public ResponseEntity<T> getById(@PathVariable Long id) {
    T entity = service.getById(id);
    return ResponseEntity.ok(entity);
  }

  // @PostMapping
  // public ResponseEntity<T> create(@RequestBody T entity) {
  //   T createdEntity = service.save(entity);
  //   return ResponseEntity.status(HttpStatus.CREATED).body(createdEntity);
  // }
  //
  // @PutMapping("/{id}")
  // public ResponseEntity<T> update(@PathVariable Long id, @RequestBody T entity) {
  //   T updatedEntity = service.update(id, entity);
  //   return ResponseEntity.ok(updatedEntity);
  // }
  //
  // @DeleteMapping("/{id}")
  // public ResponseEntity<Void> delete(@PathVariable Long id) {
  //   service.delete(id);
  //   return ResponseEntity.noContent().build();
  // }
}
