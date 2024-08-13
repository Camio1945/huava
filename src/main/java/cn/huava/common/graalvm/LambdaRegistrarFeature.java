package cn.huava.common.graalvm;

import cn.huava.common.auth.SecurityConfig;
import cn.huava.common.auth.SysPasswordAuthProvider;
import cn.huava.sys.service.SysUserLoginUserDetailsServiceImpl;
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
    RuntimeSerialization.registerLambdaCapturingClass(SysUserLoginUserDetailsServiceImpl.class);
    RuntimeSerialization.registerLambdaCapturingClass(SecurityConfig.class);
    RuntimeSerialization.registerLambdaCapturingClass(SysPasswordAuthProvider.class);
  }
}
