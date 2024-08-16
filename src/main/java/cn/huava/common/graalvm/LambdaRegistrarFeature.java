package cn.huava.common.graalvm;

import cn.huava.common.config.SecurityConfig;
import cn.huava.sys.service.SysUserLoginUserDetailsServiceImpl;
import cn.huava.sys.service.sysrefreshtoken.SysRefreshTokenAceService;
import cn.huava.sys.service.sysuser.SysUserLoginService;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeSerialization;

/**
 * Register lambda to GraalVM. Used by GraalVM.
 *
 * @author Camio1945
 */
public class LambdaRegistrarFeature implements Feature {

  @Override
  public void duringSetup(DuringSetupAccess access) {
    RuntimeSerialization.registerLambdaCapturingClass(SecurityConfig.class);
    RuntimeSerialization.registerLambdaCapturingClass(SysUserLoginService.class);
    RuntimeSerialization.registerLambdaCapturingClass(SysUserLoginUserDetailsServiceImpl.class);
    RuntimeSerialization.registerLambdaCapturingClass(SysRefreshTokenAceService.class);
  }
}
