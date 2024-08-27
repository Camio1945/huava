  
---

# v.0.1.2 - Target

Refactor the refresh token code because the last version does not support GraalVM native image.

But after a lot of trying, old problem becomes new problem, in the end, this version still does not support GraalVM native image.

I quit!!!

---

# What's wrong?

This code works fine in JVM mode, but it does not work in native image mode. No exception is thrown, but the res will lack a lot of information (e.g. from 3.04KB to 548B) in the native image mode.

```java
OAuth2Authorization res = Oauth2AuthorizationConverter.convert(oauth2AuthorizationPo);
```

---

# What have I tried?

1. [Debug Native Executables with GDB](https://www.graalvm.org/22.2/reference-manual/native-image/guides/debug-native-image-process/)

   Hard to do.

2. [Debug Native Images in VS Code](https://www.graalvm.org/22.0/tools/vscode/graalvm-extension/debugging-native-image/)

   The extension page is 404.

3. [Debug GraalVM native images in Intellij Idea](https://www.jetbrains.com/help/idea/debug-graalvm-native.html)

   Cannot debug, got this error: Error running 'native' Access is allowed from Event Dispatch Thread (EDT) only; see https://jb.gg/ ij-platform-threading for details Current thread: Thread[#210,DefaultDispatcher-worker-8,5,main] 2071755317 (EventQueue. isDispatchThread()=false) SystemEventQueueThread: Thread[#47,AWT-EventQueue-0,6,main] 316382235

4. Downgrade GraalVM 22 to GraalVM 21
