
package hw.java_core.blocking_queue;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class BlockingQueueTest {

  @Test
  void shouldFollowFifoOrder() throws InterruptedException {
    BlockingQueue<String> queue = new BlockingQueue<>(3);

    queue.enqueue("A");
    queue.enqueue("B");
    queue.enqueue("C");

    assertEquals("A", queue.dequeue());
    assertEquals("B", queue.dequeue());
    assertEquals("C", queue.dequeue());
  }

  @Test
  void shouldReturnCurrentSize() throws InterruptedException {
    BlockingQueue<Integer> queue = new BlockingQueue<>(3);

    assertEquals(0, queue.size());

    queue.enqueue(10);
    queue.enqueue(20);

    assertEquals(2, queue.size());

    queue.dequeue();

    assertEquals(1, queue.size());
  }

  @Test
  void consumerShouldWaitUntilElementIsAvailable()
      throws Exception {
    BlockingQueue<String> queue = new BlockingQueue<>(1);

    ExecutorService executor = Executors.newSingleThreadExecutor();
    CountDownLatch started = new CountDownLatch(1);

    try {
      Future<String> result = executor.submit(() -> {
        started.countDown();
        return queue.dequeue();
      });

      assertTrue(started.await(1, TimeUnit.SECONDS));

      // Потребитель должен ждать появления элемента.
      assertThrows(
          TimeoutException.class,
          () -> result.get(200, TimeUnit.MILLISECONDS)
      );

      queue.enqueue("Task");

      assertEquals(
          "Task",
          result.get(1, TimeUnit.SECONDS)
      );

      assertEquals(0, queue.size());
    } finally {
      executor.shutdownNow();
      assertTrue(executor.awaitTermination(2, TimeUnit.SECONDS));
    }
  }

  @Test
  void producerShouldWaitUntilSpaceIsAvailable()
      throws Exception {
    BlockingQueue<String> queue = new BlockingQueue<>(1);
    queue.enqueue("First");

    ExecutorService executor = Executors.newSingleThreadExecutor();
    CountDownLatch started = new CountDownLatch(1);

    try {
      Future<?> producer = executor.submit(() -> {
        started.countDown();
        queue.enqueue("Second");
        return null;
      });

      assertTrue(started.await(1, TimeUnit.SECONDS));

      // Производитель должен ждать освобождения места.
      assertThrows(
          TimeoutException.class,
          () -> producer.get(200, TimeUnit.MILLISECONDS)
      );

      assertEquals("First", queue.dequeue());

      // После освобождения места производитель продолжит работу.
      producer.get(1, TimeUnit.SECONDS);

      assertEquals(1, queue.size());
      assertEquals("Second", queue.dequeue());
    } finally {
      executor.shutdownNow();
      assertTrue(executor.awaitTermination(2, TimeUnit.SECONDS));
    }
  }

  @Test
  void shouldRejectNonPositiveCapacity() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new BlockingQueue<>(0)
    );

    assertThrows(
        IllegalArgumentException.class,
        () -> new BlockingQueue<>(-1)
    );
  }

  @Test
  void shouldRejectNullElement() {
    BlockingQueue<String> queue = new BlockingQueue<>(1);

    assertThrows(
        NullPointerException.class,
        () -> queue.enqueue(null)
    );

    assertEquals(0, queue.size());
  }

  @Test
  void shouldHandleMultipleProducersAndConsumers() throws Exception {
    int producerCount = 3;
    int consumerCount = 4;
    int itemsPerProducer = 100;
    int totalItems = producerCount * itemsPerProducer;

    BlockingQueue<Integer> queue = new BlockingQueue<>(10);

    ExecutorService executor =
        Executors.newFixedThreadPool(producerCount + consumerCount);

    ConcurrentHashMap<Integer, AtomicInteger> processed =
        new ConcurrentHashMap<>();

    CountDownLatch producersDone = new CountDownLatch(producerCount);

    try {
      for (int p = 0; p < producerCount; p++) {
        final int producerId = p;

        executor.submit(() -> {
          try {
            for (int i = 0; i < itemsPerProducer; i++) {
              try {
                queue.enqueue(
                    producerId * itemsPerProducer + i
                );
              } catch (InterruptedException e) {
                throw new RuntimeException(e);
              }
            }
          } finally {
            producersDone.countDown();
          }
        });
      }

      List<Future<?>> consumers = new ArrayList<>();

      for (int c = 0; c < consumerCount; c++) {
        consumers.add(executor.submit(() -> {
          while (true) {
            int item = queue.dequeue();

            if (item == -1) {
              break;
            }

            processed.computeIfAbsent(
                item, key -> new AtomicInteger()
            ).incrementAndGet();
          }
          return null;
        }));
      }

      assertTrue(producersDone.await(5, TimeUnit.SECONDS));

      // Все производители завершились — отправляем маркеры остановки.
      for (int c = 0; c < consumerCount; c++) {
        queue.enqueue(-1);
      }

      for (Future<?> consumer : consumers) {
        consumer.get(5, TimeUnit.SECONDS);
      }

      assertEquals(totalItems, processed.size());

      for (int i = 0; i < totalItems; i++) {
        assertEquals(1, processed.get(i).get());
      }

      assertEquals(0, queue.size());
    } finally {
      executor.shutdownNow();
      assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
    }
  }

  @Test
  void shouldRespectCapacityWithMultipleProducers()
      throws Exception {
    int producerCount = 5;
    int itemsPerProducer = 20;
    int capacity = 5;

    BlockingQueue<Integer> queue = new BlockingQueue<>(capacity);

    ExecutorService executor =
        Executors.newFixedThreadPool(producerCount);

    CountDownLatch start = new CountDownLatch(1);
    List<Future<?>> producers = new ArrayList<>();

    try {
      for (int p = 0; p < producerCount; p++) {
        final int producerId = p;

        producers.add(executor.submit(() -> {
          start.await();

          for (int i = 0; i < itemsPerProducer; i++) {
            queue.enqueue(
                producerId * itemsPerProducer + i
            );
          }

          return null;
        }));
      }

      start.countDown();

      // Без потребителя производители заполнят очередь и заблокируются.
      for (Future<?> producer : producers) {
        assertThrows(
            TimeoutException.class,
            () -> producer.get(200, TimeUnit.MILLISECONDS)
        );
        break;
      }

      assertEquals(capacity, queue.size());

      // Освобождаем место и даём производителям завершиться.
      for (int i = 0; i < producerCount * itemsPerProducer; i++) {
        queue.dequeue();

        if (i < capacity) {
          // Проверяем, что размер остаётся в допустимых пределах.
          assertTrue(queue.size() <= capacity);
        }
      }

      for (Future<?> producer : producers) {
        producer.get(5, TimeUnit.SECONDS);
      }
    } finally {
      executor.shutdownNow();
      assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
    }
  }
}
