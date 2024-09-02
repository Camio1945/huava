package cn.huava.common.graalvm;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.*;

class NativeRuntimeHintsRegistrarTest {

  private RuntimeHints runtimeHints = new RuntimeHints();

  private NativeRuntimeHintsRegistrar registrar = new NativeRuntimeHintsRegistrar();

  @Test
  void registerHints() {
    registrar.registerHints(runtimeHints, getClass().getClassLoader());
    TypeHint typeHint = runtimeHints.reflection().getTypeHint(ArrayList.class);
    assertNotNull(typeHint);
    List<ResourcePatternHints> list = runtimeHints.resources().resourcePatternHints().toList();
    assertFalse(list.isEmpty());
  }
}
