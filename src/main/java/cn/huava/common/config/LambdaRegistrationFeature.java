package cn.huava.common.config;

import cn.huava.sys.service.SysUserLoginUserDetailsServiceImpl;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeSerialization;

/**
 * Register lambda to GraalVM. Used by GraalVM.
 *
 * @author Camio1945
 */
@SuppressWarnings("unused")
public class LambdaRegistrationFeature implements Feature {

  @Override
  public void duringSetup(DuringSetupAccess access) {
    RuntimeSerialization.registerLambdaCapturingClass(SysUserLoginUserDetailsServiceImpl.class);
  }
}
