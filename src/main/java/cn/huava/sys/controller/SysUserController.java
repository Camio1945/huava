package cn.huava.sys.controller;

import cn.huava.common.controller.BaseController;
import cn.huava.common.pojo.dto.ApiResponseDataDto;
import cn.huava.sys.mapper.SysUserMapper;
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
    return new ResponseEntity<>(new ApiResponseDataDto<>(url), HttpStatus.OK);
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponseDataDto<SysUserJwtDto>> login(
      HttpServletRequest req, @RequestBody @NonNull LoginQo loginQo) {
    SysUserJwtDto sysUserJwtDto = service.login(req, loginQo);
    return new ResponseEntity<>(new ApiResponseDataDto<>(sysUserJwtDto), HttpStatus.OK);
  }

  @PostMapping("/refreshToken")
  public ResponseEntity<ApiResponseDataDto<String>> refreshToken(
      @RequestBody @NonNull String refreshToken) {
    String accessToken = service.refreshToken(refreshToken);
    return new ResponseEntity<>(new ApiResponseDataDto<>(accessToken), HttpStatus.OK);
  }
}
