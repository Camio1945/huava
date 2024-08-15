package cn.huava.common.listener;

import cn.huava.common.util.EncryptUtil;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.thread.ThreadUtil;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Service;

/**
 * @author Camio1945
 */
@Slf4j
@Service
@AllArgsConstructor
public class ApplicationEventListener implements ApplicationListener<ApplicationEvent> {

  @Override
  public void onApplicationEvent(@NonNull ApplicationEvent event) {
    if (event instanceof ApplicationReadyEvent) {
      log.info("Application started. {} is now executing.", ApplicationEventListener.class);
      initInThread();
      return;
    }
    if ((event instanceof ContextStoppedEvent) || (event instanceof ContextClosedEvent)) {
      log.info("Application stops.");
    }
  }

  private void initInThread() {
    ThreadUtil.execute(this::initEncryptUtil);
  }

  /** AES is a heavy object (take more than 1 second on my computer), so we initial it in thread. */
  private void initEncryptUtil() {
    String plainText = "hello";
    String encrypt = EncryptUtil.encrypt(plainText);
    if (encrypt.equals(plainText)) {
      log.error("EncryptUtil.encrypt is not working.");
    }
    String decrypt = EncryptUtil.decrypt(encrypt);
    if (!decrypt.equals(plainText)) {
      log.error("EncryptUtil.decrypt is not working.");
    }
  }
}
