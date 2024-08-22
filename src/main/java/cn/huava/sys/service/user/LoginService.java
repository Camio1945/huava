package cn.huava.sys.service.user;

import cn.huava.common.service.BaseService;
import cn.huava.common.service.captcha.AceCaptchaService;
import cn.huava.common.util.Fn;
import cn.huava.sys.mapper.UserMapper;
import cn.huava.sys.pojo.dto.UserJwtDto;
import cn.huava.sys.pojo.po.UserPo;
import cn.huava.sys.pojo.qo.LoginQo;
import cn.huava.sys.service.jwt.AceJwtService;
import cn.huava.sys.service.refreshtoken.AceRefreshTokenService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
class LoginService extends BaseService<UserMapper, UserPo> {

  private final AceRefreshTokenService sysRefreshTokenAceService;
  private final AceJwtService jwtAceService;
  private final AceCaptchaService aceCaptchaService;

  protected UserJwtDto login(HttpServletRequest req, LoginQo loginQo) {
    aceCaptchaService.validate(
        req, loginQo.getCaptchaCode(), loginQo.getIsCaptchaDisabledForTesting());
    String username = loginQo.getUsername();
    String password = loginQo.getPassword();
    Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
    authentication = authenticationManager().authenticate(authentication);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    UserPo userPo =
        baseMapper.selectOne(new LambdaQueryWrapper<UserPo>().eq(UserPo::getUsername, username));
    UserJwtDto userJwtDto = jwtAceService.createToken(userPo.getId());
    saveRefreshToken(username, userJwtDto);
    return userJwtDto;
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
    return Fn.getBean(AuthenticationManager.class);
  }

  private void saveRefreshToken(String username, UserJwtDto userJwtDto) {
    Wrapper<UserPo> wrapper = new LambdaQueryWrapper<UserPo>().eq(UserPo::getUsername, username);
    UserPo userPo = baseMapper.selectOne(wrapper);
    sysRefreshTokenAceService.saveRefreshToken(userPo.getId(), userJwtDto.getRefreshToken());
  }
}
