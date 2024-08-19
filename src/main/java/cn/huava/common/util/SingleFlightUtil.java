package cn.huava.common.util;

import java.util.concurrent.*;
import org.dromara.hutool.core.exception.ExceptionUtil;

/**
 * Singleton mode tool class, used to solve the problem of resource contention in multithreading.
 * <br>
 * When multiple threads request the same resource at the same time, only one thread will be
 * executed at a time, and the other threads will wait for the result of the first thread, all
 * threads return the same object. <br>
 *
 * <pre>
 * Kudos to <a href="https://pkg.go.dev/golang.org/x/sync/singleflight"> golang's singleflight</a>
 * </pre>
 *
 * @author Camio1945
 */
public class SingleFlightUtil {

  @SuppressWarnings("rawtypes")
  private static final ConcurrentHashMap<String, FutureTask> KEY_TO_FUTURE_TASK_MAP =
      new ConcurrentHashMap<>();

  private SingleFlightUtil() {}

  @SuppressWarnings("unchecked")
  public static <T> T execute(String key, Callable<T> fn) {
    FutureTask<T> task = new FutureTask<>(fn);
    FutureTask<T> existingTask = KEY_TO_FUTURE_TASK_MAP.putIfAbsent(key, task);
    try {
      // 如果 key 不存在，则 existingTask 为 null，此时需要执行 task.run()，否则直接返回 existingTask.get()
      if (existingTask == null) {
        T res;
        try {
          task.run();
          res = task.get();
        } finally {
          KEY_TO_FUTURE_TASK_MAP.remove(key);
        }
        return res;
      } else {
        return existingTask.get();
      }
    } catch (InterruptedException e) {
      // The code here is not covered during unit testing and can be ignored
      Thread.currentThread().interrupt();
      throw ExceptionUtil.wrapRuntime(e);
    } catch (ExecutionException e) {
      // The code here is not covered during unit testing and can be ignored
      throw ExceptionUtil.wrapRuntime(e);
    }
  }
}
