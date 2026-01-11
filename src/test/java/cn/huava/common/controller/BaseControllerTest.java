package cn.huava.common.controller;

import static org.assertj.core.api.Assertions.assertThat;

import cn.huava.common.WithSpringBootTestAnnotation;
import cn.huava.sys.mapper.RolePermMapper;
import cn.huava.sys.pojo.po.RolePermPo;
import cn.huava.sys.service.roleperm.AceRolePermService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

///
/// This class is used to increase test coverage.
///
@AutoConfigureMockMvc
@Rollback
@Transactional
class BaseControllerTest extends WithSpringBootTestAnnotation {
  @Autowired AceRolePermService aceRolePermService;

  @Test
  @SneakyThrows
  void should_not_found() {
    TempController tempController = new TempController();
    tempController.service = aceRolePermService;
    ResponseEntity<RolePermPo> res = tempController.getById(999999999999999999L);
    assertThat(res.getBody()).isNull();
  }

  private static class TempController
      extends BaseController<AceRolePermService, RolePermMapper, RolePermPo> {}
}
