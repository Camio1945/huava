package cn.huava.common.annotation;

import cn.huava.common.enumeration.AccessModifierEnum;

/**
 * 表示某个类、字段、方法的可见范围被提高了，以便更好地执行测试。<br>
 * 参考自：org.assertj.core.util.VisibleForTesting
 *
 * @author Camio1945
 */
public @interface VisibleForTesting {

  /** 原本应该是哪种访问修饰符，见 {@link AccessModifierEnum} */
  AccessModifierEnum original();
}
