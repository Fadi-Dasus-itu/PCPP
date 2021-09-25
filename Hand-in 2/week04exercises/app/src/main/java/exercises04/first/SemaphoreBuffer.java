package exercises04.first;

import java.util.Random;
import java.util.concurrent.Semaphore;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class SemaphoreBuffer implements BoundedBufferInterface<Integer> {

    private static volatile Queue list = null;
    private Semaphore isEmpty;
    private Semaphore isFull;


    public SemaphoreBuffer(int size, Semaphore isFull, Semaphore isEmpty, Semaphore binarySemaphore) throws InterruptedException {
        // ensure full publication of all the variables at creation time
        if (binarySemaphore.availablePermits() != 0) {
            try {
                binarySemaphore.acquire();
                this.list = new Queue(size);
                this.isFull = isFull;
                this.isEmpty = isEmpty;
            } finally {
                binarySemaphore.release();
            }
        }
    }

    @Override
    @GuardedBy("isEmpty")
    public Integer take() {
        return list.dequeue();
    }

    @Override
    @GuardedBy("isFull")
    public void insert(Integer elem) {
        list.enqueue(elem);
    }

    public Integer consume() throws InterruptedException {
        if (!(isEmpty.availablePermits() == 0) && !list.isEmpty()) {
            try {
                isEmpty.acquire();
                var integer = this.take();
                System.out.println(Thread.currentThread() + " acquired the isEmpty semaphore and called take " + "the taken element is " + integer);
                return integer;

            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            } finally {
                System.out.println(Thread.currentThread() + " released the isFull semaphore");
                isFull.release();
            }
        } else {
            System.out.println(Thread.currentThread() + " the isEmpty semaphore is already acquired,or the list is empty I could not call the take, hence I will wait ");
        }
        return null;
    }

    public void produce() throws InterruptedException {
        if (!(isFull.availablePermits() == 0) && !list.isFull()) {
            try {
                isFull.acquire();
                insert(new Random().nextInt(50));
                System.out.println(Thread.currentThread() + " acquired the isFull semaphore and called insert ");
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            } finally {
                System.out.println(Thread.currentThread() + " released the isEmpty semaphore");
                isEmpty.release();
            }
        } else {
            System.out.println(Thread.currentThread() + " the isFull semaphore is already acquired or the list is full, I could not call the insert, hence I will wait ");
        }
    }

    private class Queue {

        private static final Semaphore binarySemaphoreWriterLocker = new Semaphore(1);

        private static final Semaphore binarySemaphoreReaderLocker = new Semaphore(1);

        private volatile int[] items;

        private volatile int head = -1;

        private volatile int tail = -1;

        private volatile int numOfItems = 0; // this field makes the implementation easy, but we can implement even without it.

        public Queue(int size) {
            this.items = new int[size];
        }

        public void enqueue(int item) {
            try {
                binarySemaphoreWriterLocker.acquire();
                if (isFull())
                    throw new RuntimeException("Queue is full");
                if (tail == items.length - 1) // deal with circular case
                    tail = -1;
                items[++tail] = item;
                numOfItems++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                binarySemaphoreWriterLocker.release();
            }
        }

        public int dequeue() {
            int element = 0;
            try {
                binarySemaphoreReaderLocker.acquire();
                if (isEmpty())
                    throw new RuntimeException("Queue is empty");
                if (head == items.length - 1)
                    head = -1;
                numOfItems--;
                return items[++head];
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                binarySemaphoreReaderLocker.release();
            }
            return element;
        }

        public boolean isFull() {
            return numOfItems == items.length;
        }

        public boolean isEmpty() {
            return numOfItems == 0;
        }
    }

}
