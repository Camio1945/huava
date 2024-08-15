package cn.huava.common.graalvm;

import cn.huava.common.auth.SecurityConfig;
import cn.huava.common.auth.SysPasswordAuthProvider;
import cn.huava.common.listener.ApplicationEventListener;
import cn.huava.sys.service.SysUserLoginUserDetailsServiceImpl;
import java.security.Security;
import java.util.Set;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.graalvm.nativeimage.hosted.*;

/**
 * Custom GraalVM feature, like register lambda . <br>
 * Used by GraalVM.
 *
 * @author Camio1945
 */
public class CustomGraalvmFeature implements Feature {

  @Override
  public void afterRegistration(AfterRegistrationAccess access) {
    // To solve the UnsupportedFeatureError of bouncycastle. (pom.xml has to do something too)
    RuntimeClassInitialization.initializeAtBuildTime("org.bouncycastle");
    Security.addProvider(new BouncyCastleProvider());
  }

  @Override
  public void duringSetup(DuringSetupAccess access) {
    // It is suggested to list those classes alphabetically.
    Set<Class<?>> classes =
        Set.of(
            ApplicationEventListener.class,
            SecurityConfig.class,
            SysPasswordAuthProvider.class,
            SysUserLoginUserDetailsServiceImpl.class);
    for (Class<?> aClass : classes) {
      RuntimeSerialization.registerLambdaCapturingClass(aClass);
    }
  }
}
