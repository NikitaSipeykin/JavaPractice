package hw.java_core.blocking_queue;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class BlockingQueue<T> {
  private final Deque<T> queue;
  private final int capacity;

  public BlockingQueue(int capacity) {
    if (capacity <= 0){
      throw new IllegalArgumentException("Capacity must be positive");
    }

    this.queue = new ArrayDeque<>();
    this.capacity = capacity;
  }

  public synchronized T dequeue() throws InterruptedException{
    while (queue.isEmpty()){
      wait();
    }

    T element = queue.pollFirst();
    notifyAll();

    return element;
  }

  public synchronized void enqueue(T element) throws InterruptedException{

    Objects.requireNonNull(element, "Element must not be null");

    while (queue.size() >= capacity){
      wait();
    }

    queue.addLast(element);
    notifyAll();
  }

  public synchronized int size(){
    return queue.size();
  }
}
