package cn.huava.common.util;

import cn.huava.sys.pojo.po.UserPo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import lombok.NonNull;
import org.dromara.hutool.core.bean.BeanUtil;
import org.dromara.hutool.core.io.file.FileUtil;
import org.dromara.hutool.core.text.*;
import org.dromara.hutool.extra.spring.SpringUtil;
import org.springframework.util.StringUtils;

/**
 * Fn is short for function, like the same button on a laptop; it provides a facade for common
 * utils. <br>
 * Note 1: This class has no business code itself; it only combines the utilities that other classes
 * have already provided.<br>
 * Note 2: Because of note 1, all the methods in this class have no comments; see the original class
 * instead.<br>
 * Note 3: The methods from Assert are not included in this class, isNotBlank and notBlank will
 * cause confusion.<br>
 * Note 4: If a method has been used more than once, it's recommended to included in this class.<br>
 *
 * <pre>
 * Original story:
 *    I like to use StrUtil.isBlank(...) , but when I use it, the SonarLint will warn:
 *       Use static access with "org.dromara.hutool.core.text.StrValidator" for "isBlank".
 *    because StrUtil extends StrValidator, and the isBlank(...) is in StrValidator.
 *    So based on SonarLink, I have to use StrValidator.isBlank(...), which is not convenient.
 *    Then I saw the Fn button on my laptop and decided to create this class.
 *    I know some articles don't recommend using a large util class, but after weighing the pros and cons, I decided to use it anyway.
 * </pre>
 *
 * @author Camio1945
 */
public class Fn {
  private Fn() {}

  public static boolean isBlank(final CharSequence str) {
    return StrValidator.isBlank(str);
  }

  public static boolean isNotBlank(final CharSequence str) {
    return StrValidator.isNotBlank(str);
  }

  public static String cleanPath(String path) {
    return StringUtils.cleanPath(path);
  }

  public static String format(final CharSequence template, final Object... params) {
    return CharSequenceUtil.format(template, params);
  }

  public static <T> T getBean(final Class<T> clazz) {
    return SpringUtil.getBean(clazz);
  }

  public static boolean exists(final String path) {
    return FileUtil.exists(path);
  }

  public static boolean exists(final File file) {
    return FileUtil.exists(file);
  }

  public static UserPo getLoginUser() {
    return LoginUtil.getLoginUser();
  }

  public static @NonNull HttpServletRequest getRequest() {
    return HttpServletUtil.getRequest();
  }

  public static <T> T toBean(final Object source, final Class<T> clazz) {
    return BeanUtil.toBean(source, clazz);
  }

  public static <T> LambdaQueryWrapper<T> buildUndeletedWrapper(
      @NonNull final SFunction<T, ?> deleteInfoColumn) {
    return MybatisPlusUtil.buildUndeletedWrapper(deleteInfoColumn);
  }

  public static String encryptPassword(@NonNull final String str) {
    return EncryptUtil.encryptPassword(str);
  }
}
