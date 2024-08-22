package cn.huava.common.graalvm;

import cn.huava.common.pojo.po.BasePo;
import cn.huava.common.pojo.qo.PageQo;
import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import lombok.NonNull;
import org.apache.ibatis.javassist.util.proxy.ProxyFactory;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.scripting.defaults.RawLanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.springframework.aot.hint.*;

/**
 * Register resources and classes for GraalVM native image.
 *
 * @author Camio1945
 */
public class NativeRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

  @Override
  public void registerHints(@NonNull RuntimeHints hints, ClassLoader classLoader) {
    registerResources(hints);
    registerClasses(hints);
  }

  private void registerResources(@NonNull RuntimeHints hints) {
    Stream.of(
            "org/apache/ibatis/builder/xml/*.dtd",
            "org/apache/ibatis/builder/xml/*.xsd",
            "static_captcha/*")
        .forEach(hints.resources()::registerPattern);
  }

  private void registerClasses(@NonNull RuntimeHints hints) {
    Set<Class<?>> classes =
        Set.of(
            // huava
            BasePo.class,
            PageQo.class,
            // http response for gzip
            GZIPInputStream.class,
            // java
            ArrayList.class,
            // ibatis, mybatis, mybatis-plus
            AbstractLambdaWrapper.class,
            AbstractWrapper.class,
            LambdaQueryWrapper.class,
            MybatisXMLLanguageDriver.class,
            ProxyFactory.class,
            RawLanguageDriver.class,
            Slf4jImpl.class,
            StdOutImpl.class,
            UpdateWrapper.class,
            Wrapper.class,
            XMLLanguageDriver.class,
            // jackson
            ToStringSerializer.class);
    classes.forEach(c -> hints.reflection().registerType(c, MemberCategory.values()));
  }
}
