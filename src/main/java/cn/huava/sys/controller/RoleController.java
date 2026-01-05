package cn.huava.sys.controller;

import cn.huava.common.controller.BaseController;
import cn.huava.common.pojo.dto.PageDto;
import cn.huava.common.pojo.qo.PageQo;
import cn.huava.sys.mapper.RoleMapper;
import cn.huava.sys.pojo.po.RolePo;
import cn.huava.sys.pojo.qo.SetPermQo;
import cn.huava.sys.service.role.AceRoleService;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 角色控制器
 *
 * @author Camio1945
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sys/role")
public class RoleController extends BaseController<AceRoleService, RoleMapper, RolePo> {

  @GetMapping("/isNameExists")
  public ResponseEntity<Boolean> isNameExists(final Long id, @NonNull final String name) {
    return ResponseEntity.ok(service.isNameExists(id, name));
  }

  @GetMapping("/page")
  public ResponseEntity<PageDto<RolePo>> page(
      @NonNull final PageQo<RolePo> pageQo, @NonNull final RolePo params) {
    PageDto<RolePo> pageDto = service.rolePage(pageQo, params);
    return ResponseEntity.ok(pageDto);
  }

  @PostMapping("/setPerm")
  public ResponseEntity<Void> setPerm(@RequestBody @NonNull @Validated final SetPermQo setPermQo) {
    service.setPerm(setPermQo);
    return ResponseEntity.ok(null);
  }

  @GetMapping("/getPerm/{id}")
  public ResponseEntity<List<Long>> getPerm(@PathVariable @NonNull final Long id) {
    return ResponseEntity.ok(service.getPerm(id));
  }
}
