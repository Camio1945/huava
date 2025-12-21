package cn.huava.common.graalvm;

import cn.hutool.v7.core.reflect.ClassUtil;
import cn.hutool.v7.db.sql.BoundSql;
import org.jspecify.annotations.NonNull;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import tools.jackson.databind.ser.std.ToStringSerializer;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

/**
 * 为 GraalVM native image 注册资源和类。<br>
 * 当前类你理解为一个附属类，它在 {@link RuntimeHintsRegistrarConfig} 的注解中用到。
 *
 * @author Camio1945
 */
public class NativeRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

  @Override
  public void registerHints(@NonNull RuntimeHints hints, ClassLoader classLoader) {
    registerResources(hints);
    registerClasses(hints);
  }

  protected void registerResources(@NonNull RuntimeHints hints) {
    Stream.of("org/apache/ibatis/builder/xml/*.dtd", "org/apache/ibatis/builder/xml/*.xsd", "static_captcha/*", "*.yml", "*.yaml", "*.properties").forEach(hints.resources()::registerPattern);
  }

  protected void registerClasses(@NonNull RuntimeHints hints) {
    Set<Class<?>> classes = new HashSet<>();
    addMiscellaneousClasses(classes);
    addAwtClasses(classes);
    addIbatisClasses(classes);
    addHuavaClasses(classes);
    classes.forEach(c -> hints.reflection().registerType(c, MemberCategory.values()));
  }

  protected void addMiscellaneousClasses(Set<Class<?>> classes) {
    Set<Class<?>> miscellaneousClasses = Set.of(
      // http response for gzip
      GZIPInputStream.class,
      // Java
      ArrayList.class,
      // Jackson
      ToStringSerializer.class);
    classes.addAll(miscellaneousClasses);
  }

  protected void addAwtClasses(Set<Class<?>> classes) {
    Set<Class<?>> awtClasses =
        new HashSet<>(
            Set.of(
                GraphicsEnvironment.class,
                Toolkit.class,
                Font.class,
                java.awt.image.BufferedImage.class,
                java.awt.Color.class,
                java.awt.BasicStroke.class,
                java.awt.RenderingHints.class,
                java.awt.image.ColorModel.class,
                java.awt.image.ComponentColorModel.class,
                java.awt.image.DirectColorModel.class,
                java.awt.image.IndexColorModel.class,
                java.awt.image.Raster.class,
                java.awt.image.SampleModel.class,
                java.awt.image.SinglePixelPackedSampleModel.class));
    try {
      awtClasses.add(Class.forName("sun.awt.X11.XToolkit"));
      awtClasses.add(Class.forName("sun.java2d.InvalidPipeException"));
      awtClasses.add(Class.forName("sun.java2d.loops.Blit"));
      awtClasses.add(Class.forName("sun.java2d.loops.BlitBg"));
      awtClasses.add(Class.forName("sun.java2d.loops.ScaledBlit"));
      awtClasses.add(Class.forName("sun.java2d.loops.FillRect"));
      awtClasses.add(Class.forName("sun.java2d.loops.FillSpans"));
      awtClasses.add(Class.forName("sun.java2d.loops.FillParallelogram"));
    } catch (ClassNotFoundException e) {
      // do nothing
    }
    classes.addAll(awtClasses);
  }

  protected void addIbatisClasses(Set<Class<?>> classes) {
    // ibatis, mybatis, mybatis-plus
    Set<Class<?>> ibatisClasses = Set.of(BoundSql.class, ProxyFactory.class);
    classes.addAll(ibatisClasses);
  }

  protected void addHuavaClasses(Set<Class<?>> classes) {
    classes.addAll(ClassUtil.scanPackage("cn.huava").stream().toList());
  }
}
