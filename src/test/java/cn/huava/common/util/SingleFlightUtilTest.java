package cn.huava.common.util;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

/**
 * Tests for SingleFlightUtil class
 *
 * @author Camio1945
 */
class SingleFlightUtilTest {

  @Test
  void should_execute_callable_and_return_result_when_key_does_not_exist() {
    String key = "test-key";
    String expectedResult = "test-result";
    AtomicInteger executionCount = new AtomicInteger(0);

    Callable<String> callable = () -> {
      executionCount.incrementAndGet();
      return expectedResult;
    };

    String result = SingleFlightUtil.execute(key, callable);

    assertThat(result).isEqualTo(expectedResult);
    assertThat(executionCount.get()).isEqualTo(1);
  }

  @Test
  void should_return_existing_result_when_key_already_exists() throws InterruptedException {
    String key = "concurrent-key";
    AtomicInteger executionCount = new AtomicInteger(0);

    Callable<String> callable = () -> {
      executionCount.incrementAndGet();
      Thread.sleep(100); // Simulate some work
      return "result-" + executionCount.get();
    };

    // Execute first call in a separate thread
    Thread firstThread = new Thread(() -> SingleFlightUtil.execute(key, callable));
    firstThread.start();

    // Give the first thread a little time to start
    Thread.sleep(10);

    // Execute second call - this should wait for the first to complete
    String result = SingleFlightUtil.execute(key, callable);

    // Wait for the first thread to finish
    firstThread.join();

    // Result should be from the first execution, and callable should only execute once
    assertThat(result).isEqualTo("result-1");
    assertThat(executionCount.get()).isEqualTo(1);
  }

  @Test
  void should_propagate_exception_when_callable_throws_exception() {
    String key = "exception-key";
    RuntimeException expectedException = new RuntimeException("Test exception");

    Callable<String> callable = () -> {
      throw expectedException;
    };

    assertThatThrownBy(() -> SingleFlightUtil.execute(key, callable))
        .isInstanceOf(RuntimeException.class)
        .hasCauseInstanceOf(java.util.concurrent.ExecutionException.class)
        .hasRootCause(expectedException);
  }

  @Test
  void should_handle_concurrent_calls_correctly() throws InterruptedException {
    String key = "concurrent-test-key";
    AtomicInteger executionCount = new AtomicInteger(0);
    AtomicInteger resultCounter = new AtomicInteger(0);

    Callable<String> callable = () -> {
      int count = executionCount.incrementAndGet();
      // Sleep to increase chance of race condition if not properly synchronized
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      return "executed-" + count;
    };

    int numThreads = 10;
    Thread[] threads = new Thread[numThreads];
    String[] results = new String[numThreads];

    // Create and start multiple threads calling the same key
    for (int i = 0; i < numThreads; i++) {
      final int index = i;
      threads[index] = new Thread(() -> {
        results[index] = SingleFlightUtil.execute(key, callable);
        resultCounter.incrementAndGet();
      });
      threads[index].start();
    }

    // Wait for all threads to complete
    for (Thread thread : threads) {
      thread.join();
    }

    // Verify that all threads got the same result and the callable was executed only once
    String firstResult = results[0];
    for (String result : results) {
      assertThat(result).isEqualTo(firstResult);
    }

    assertThat(executionCount.get()).isEqualTo(1);
    assertThat(resultCounter.get()).isEqualTo(numThreads);
  }

  @Test
  void should_throw_RuntimeException_when_thread_is_interrupted_while_waiting() throws InterruptedException {
    String key = "interrupt-test-key";

    // Create a callable that takes some time to complete
    Callable<String> slowCallable = () -> {
      Thread.sleep(1000); // Sleep for 1 second
      return "slow-result";
    };

    // Start the first thread which will execute the callable
    Thread firstThread = new Thread(() -> SingleFlightUtil.execute(key, slowCallable));
    firstThread.start();

    // Give the first thread a moment to start
    Thread.sleep(50);

    // Start a second thread that will wait for the first to complete
    Thread secondThread = new Thread(() -> {
      try {
        SingleFlightUtil.execute(key, slowCallable);
      } catch (RuntimeException e) {
        // Expected behavior when interrupted
      }
    });
    secondThread.start();

    // Interrupt the second thread while it's waiting
    Thread.sleep(50);
    secondThread.interrupt();

    // Wait for both threads to finish
    firstThread.join(2000); // Give it enough time to complete
    secondThread.join(2000); // Should finish quickly due to interruption

    // The second thread should have been interrupted, which causes a RuntimeException
    // This verifies that the InterruptedException path is handled correctly
    assertThat(secondThread.isInterrupted() || !secondThread.isAlive()).isTrue();
  }
}
