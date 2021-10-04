package exercises04.first.version2;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Semaphore;
import javax.annotation.concurrent.GuardedBy;

import com.google.common.collect.EvictingQueue;

public class BufferBuiltInQueue implements BoundedBufferInterface<Integer> {
    private volatile Queue<Integer> list = null;
    private final Semaphore mutex = new Semaphore(1);
    private int size;
    private final Semaphore numAvailableSpaces = new Semaphore(10);
    private Semaphore numItemsInTheBuffer = new Semaphore(0);


    public BufferBuiltInQueue(int size) throws InterruptedException {
        this.size = size;
        this.list = EvictingQueue.create(size);
    }

    @Override
    @GuardedBy("isEmpty")
    public Integer take() {
        return list.remove();
    }

    @Override
    @GuardedBy("isFull")
    public void insert(Integer elem) {
        list.add(elem);
    }

    public Integer consume() {
            try {
                numItemsInTheBuffer.acquire();
                mutex.acquire();
                var integer = this.take();
                System.out.println(Thread.currentThread() + " acquired the isEmpty semaphore and called take " + "the taken element is " + integer);
                return integer;

            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            } finally {
                System.out.println(Thread.currentThread() + " released the isFull semaphore and the mutex");
                mutex.release();
                numAvailableSpaces.release();
            }
        return null;
    }

    public void produce() {
            try {
                numAvailableSpaces.acquire();
                mutex.acquire();
                insert(new Random().nextInt(50));

                System.out.println(Thread.currentThread() + " acquired the isFull semaphore and called insert ");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                System.out.println(Thread.currentThread() + " released the isEmpty semaphore and the mutex");
                mutex.release();
                numItemsInTheBuffer.release();
            }
    }

}
