package cn.huava.sys.service.sysuser;

import cn.huava.common.service.BaseService;
import cn.huava.common.service.captcha.AceCaptchaService;
import cn.huava.sys.mapper.SysUserMapper;
import cn.huava.sys.pojo.dto.SysUserJwtDto;
import cn.huava.sys.pojo.po.SysUserPo;
import cn.huava.sys.pojo.qo.LoginQo;
import cn.huava.sys.service.jwt.AceJwtService;
import cn.huava.sys.service.sysrefreshtoken.AceSysRefreshTokenService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.extra.spring.SpringUtil;
import org.dromara.hutool.json.JSONUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * @author Camio1945
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService extends BaseService<SysUserMapper, SysUserPo> {

  private final AceSysRefreshTokenService sysRefreshTokenAceService;
  private final AceJwtService jwtAceService;
  private final AceCaptchaService aceCaptchaService;

  protected SysUserJwtDto login(HttpServletRequest req, LoginQo loginQo) {
    aceCaptchaService.validate(req, loginQo.getCaptchaCode());
    String username = loginQo.getUsername();
    String password = loginQo.getPassword();
    Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
    authentication = authenticationManager().authenticate(authentication);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    SysUserPo sysUserPo =
        baseMapper.selectOne(
            new LambdaQueryWrapper<SysUserPo>().eq(SysUserPo::getUsername, username));
    SysUserJwtDto sysUserJwtDto = jwtAceService.createToken(sysUserPo.getId());
    saveRefreshToken(username, sysUserJwtDto);
    return sysUserJwtDto;
  }

  /**
   * Note 1: Don't inject this like the ${@link #sysRefreshTokenAceService} or {@link
   * #jwtAceService}, because there will be a circular reference.<br>
   * Note 2: Don't use @Lazy annotation, it works fine in JVM mode, but will fail in GraalVM native
   * image mode, cause some error like "Caused by: java.lang.ClassCastException:
   * org.springframework.aop.framework.CglibAopProxy$SerializableNoOp cannot be cast to
   * org.springframework.cglib.proxy.Dispatcher"
   */
  private AuthenticationManager authenticationManager() {
    return SpringUtil.getBean(AuthenticationManager.class);
  }

  private void saveRefreshToken(String username, SysUserJwtDto sysUserJwtDto) {
    Wrapper<SysUserPo> wrapper =
        new LambdaQueryWrapper<SysUserPo>().eq(SysUserPo::getUsername, username);
    SysUserPo sysUser = baseMapper.selectOne(wrapper);
    sysRefreshTokenAceService.saveRefreshToken(sysUser.getId(), sysUserJwtDto.getRefreshToken());
  }
}
