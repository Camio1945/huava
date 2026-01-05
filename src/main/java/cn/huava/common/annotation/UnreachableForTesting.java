package cn.huava.common.annotation;

/**
 * 表示某个类、字段、方法、或方法中的某段代码在测试时是不可达的。<br>
 * 比如有些代码，只在 GraalVM 环境下才会执行到。
 *
 * @author Camio1945
 */
public @interface UnreachableForTesting {
  /** 不可达的原因 */
  String value() default "";
}
