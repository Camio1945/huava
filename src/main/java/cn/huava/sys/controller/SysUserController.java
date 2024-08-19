package cn.huava.sys.controller;

import cn.huava.common.controller.BaseController;
import cn.huava.sys.mapper.SysUserMapper;
import cn.huava.sys.pojo.dto.ApiResponseDataDto;
import cn.huava.sys.pojo.dto.SysUserJwtDto;
import cn.huava.sys.pojo.po.SysUserPo;
import cn.huava.sys.pojo.qo.LoginQo;
import cn.huava.sys.service.sysuser.AceSysUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.text.CharSequenceUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Camio1945
 */
@Slf4j
@RestController
@RequestMapping("/sys/user")
public class SysUserController extends BaseController<AceSysUserService, SysUserMapper, SysUserPo> {

  @GetMapping("/code")
  public ResponseEntity<ApiResponseDataDto<String>> code(@NonNull HttpServletRequest req) {
    String url =
        CharSequenceUtil.format(
            "{}://{}:{}/captcha", req.getScheme(), req.getServerName(), req.getServerPort());
    ApiResponseDataDto<String> res = new ApiResponseDataDto<>(url);
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @PostMapping("/login")
  public ResponseEntity<SysUserJwtDto> login(HttpServletRequest req, @RequestBody @NonNull LoginQo loginQo) {
    SysUserJwtDto sysUserJwtDto = service.login(req, loginQo);
    return new ResponseEntity<>(sysUserJwtDto, HttpStatus.OK);
  }

  @PostMapping("/refreshToken")
  public ResponseEntity<String> refreshToken(@RequestBody @NonNull String refreshToken) {
    String accessToken = service.refreshToken(refreshToken);
    return new ResponseEntity<>(accessToken, HttpStatus.OK);
  }
}
