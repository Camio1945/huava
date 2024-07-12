package cn.huava.sys.controller;

import cn.huava.sys.pojo.po.SysTenant;
import cn.huava.sys.service.ISysTenantService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Camio1945
 */
@RestController
@RequestMapping("/sys/tenant")
public class SysTenantController {
  private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SysTenantController.class);
  @Autowired private ISysTenantService sysTenantService;

  @GetMapping("/{id}")
  public SysTenant getById(@PathVariable Long id) {
    return sysTenantService.getById(id);
  }

  @PostMapping
  public boolean add(@RequestBody SysTenant sysTenant) {
    return sysTenantService.add(sysTenant);
  }

  @PatchMapping
  public boolean update(@RequestBody SysTenant sysTenant) {
    System.out.println("System.out.println 中文测试");
    LOGGER.info("LOGGER.info 中文测试");
    return sysTenantService.update(sysTenant);
  }

  @DeleteMapping("/{id}")
  public boolean delete(@PathVariable Long id) {
    return sysTenantService.delete(id);
  }
}
