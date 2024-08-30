package cn.huava.sys.controller;

import cn.huava.common.controller.BaseController;
import cn.huava.sys.mapper.PermMapper;
import cn.huava.sys.pojo.dto.PermDto;
import cn.huava.sys.pojo.po.PermPo;
import cn.huava.sys.service.perm.AcePermService;
import java.util.List;
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
@RequestMapping("/sys/perm")
public class PermController extends BaseController<AcePermService, PermMapper, PermPo> {

  /** Uses in menu page */
  @GetMapping("/getAll")
  public ResponseEntity<List<PermDto>> getAll(boolean excludeElement) {
    return ResponseEntity.ok(service.getAllPerm(excludeElement));
  }
}
