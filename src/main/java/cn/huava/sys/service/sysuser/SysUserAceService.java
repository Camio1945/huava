package cn.huava.sys.service.sysuser;

import cn.huava.sys.mapper.SysUserMapper;
import cn.huava.sys.pojo.po.SysUser;
import cn.huava.sys.pojo.qo.LoginQo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.io.IOException;
import javax.security.auth.login.FailedLoginException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Camio1945
 */
@Slf4j
@Service
@AllArgsConstructor
public class SysUserAceService extends ServiceImpl<SysUserMapper, SysUser> {
  private final SysUserLoginService sysUserLoginService;
  private final SysUserRefreshTokenService sysUserRefreshTokenService;

  public String login(@NonNull LoginQo loginQo) throws IOException, FailedLoginException {
    return sysUserLoginService.login(loginQo);
  }

  public String refreshToken(@NonNull String refreshToken)
      throws IOException, FailedLoginException {
    return sysUserRefreshTokenService.refreshToken(refreshToken);
  }
}
