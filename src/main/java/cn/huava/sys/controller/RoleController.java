package cn.huava.sys.controller;

import cn.huava.common.controller.BaseController;
import cn.huava.common.pojo.dto.PageDto;
import cn.huava.common.pojo.qo.PageQo;
import cn.huava.sys.mapper.RoleMapper;
import cn.huava.sys.pojo.po.RolePo;
import cn.huava.sys.pojo.qo.SetPermQo;
import cn.huava.sys.service.role.AceRoleService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 角色控制器
 *
 * @author Camio1945
 */
@Slf4j
@NullMarked
@RestController
@RequiredArgsConstructor
@RequestMapping("/sys/role")
public class RoleController extends BaseController<AceRoleService, RoleMapper, RolePo> {

  @GetMapping("/isNameExists")
  public ResponseEntity<Boolean> isNameExists(@Nullable final Long id, final String name) {
    return ResponseEntity.ok(service.isNameExists(id, name));
  }

  @GetMapping("/page")
  public ResponseEntity<PageDto<RolePo>> page(final PageQo<RolePo> pageQo, final RolePo params) {
    PageDto<RolePo> pageDto = service.rolePage(pageQo, params);
    return ResponseEntity.ok(pageDto);
  }

  @PostMapping("/setPerm")
  public ResponseEntity<Void> setPerm(@RequestBody @Validated final SetPermQo setPermQo) {
    service.setPerm(setPermQo);
    return ResponseEntity.ok(null);
  }

  @GetMapping("/getPerm/{id}")
  public ResponseEntity<List<Long>> getPerm(@PathVariable final Long id) {
    return ResponseEntity.ok(service.getPerm(id));
  }
}
