package cn.huava.sys.controller;

import cn.huava.common.controller.BaseController;
import cn.huava.common.pojo.dto.PageDto;
import cn.huava.common.pojo.dto.ResDto;
import cn.huava.common.pojo.qo.PageQo;
import cn.huava.sys.mapper.RoleMapper;
import cn.huava.sys.pojo.po.RolePo;
import cn.huava.sys.service.role.AceRoleService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Camio1945
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sys/role")
public class RoleController extends BaseController<AceRoleService, RoleMapper, RolePo> {

  @GetMapping("/isNameExists")
  public ResponseEntity<ResDto<Boolean>> isNameExists(final Long id, @NonNull final String name) {
    return ResponseEntity.ok(new ResDto<>(service.isNameExists(id, name)));
  }

  @GetMapping("/page")
  public ResponseEntity<ResDto<PageDto<RolePo>>> page(
      @NonNull final PageQo pageQo, @NonNull final RolePo params) {
    PageDto<RolePo> pageDto = service.rolePage(pageQo, params);
    return ResponseEntity.ok(new ResDto<>(pageDto));
  }
}
