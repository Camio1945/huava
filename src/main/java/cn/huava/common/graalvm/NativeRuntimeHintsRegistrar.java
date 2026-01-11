package cn.huava.common.graalvm;

import cn.hutool.v7.core.reflect.ClassUtil;
import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.apache.ibatis.javassist.util.proxy.ProxyFactory;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.scripting.defaults.RawLanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.aot.hint.*;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * 为 GraalVM native image 注册资源和类。<br>
 * 当前类你理解为一个附属类，它在 {@link RuntimeHintsRegistrarConfig} 的注解中用到。
 *
 * @author Camio1945
 */
@NullMarked
public class NativeRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

  @Override
  public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
    registerResources(hints);
    registerClasses(hints);
  }

  protected void registerResources(RuntimeHints hints) {
    Stream.of(
            "org/apache/ibatis/builder/xml/*.dtd",
            "org/apache/ibatis/builder/xml/*.xsd",
            "static_captcha/*",
            "*.yml",
            "*.yaml",
            "*.properties")
        .forEach(hints.resources()::registerPattern);
  }

  protected void registerClasses(RuntimeHints hints) {
    Set<Class<?>> classes = new HashSet<>();
    addMiscellaneousClasses(classes);
    addIbatisClasses(classes);
    addHuavaClasses(classes);
    classes.forEach(c -> hints.reflection().registerType(c, MemberCategory.values()));
  }

  protected void addMiscellaneousClasses(Set<Class<?>> classes) {
    Set<Class<?>> miscellaneousClasses =
        Set.of(
            // http response for gzip
            GZIPInputStream.class,
            // Java
            ArrayList.class,
            // Jackson
            ToStringSerializer.class);
    classes.addAll(miscellaneousClasses);
  }

  protected void addIbatisClasses(Set<Class<?>> classes) {
    // ibatis, mybatis, mybatis-plus
    Set<Class<?>> ibatisClasses =
        Set.of(
            AbstractLambdaWrapper.class,
            AbstractWrapper.class,
            BoundSql.class,
            SqlSessionTemplate.class,
            LambdaQueryWrapper.class,
            MybatisPlusInterceptor.class,
            MybatisXMLLanguageDriver.class,
            ProxyFactory.class,
            RawLanguageDriver.class,
            Slf4jImpl.class,
            StdOutImpl.class,
            UpdateWrapper.class,
            Wrapper.class,
            XMLLanguageDriver.class);
    classes.addAll(ibatisClasses);
  }

  protected void addHuavaClasses(Set<Class<?>> classes) {
    classes.addAll(ClassUtil.scanPackage("cn.huava").stream().toList());
  }
}
