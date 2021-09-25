// For week 1
// sestoft@itu.dk * 2014-08-21
// raup@itu.dk * 2021-08-27
package exercises01;

import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestLongCounterExperiments {

    private final ReentrantLock lock = new ReentrantLock();
    LongCounter lc = new LongCounter();
    int counts = 10_000_000;

    public TestLongCounterExperiments() {
        Thread t1 = new Thread(() -> {
            incrementTheCount();
        });
        Thread t2 = new Thread(() -> {
            incrementTheCount();
        });

        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException exn) {
            System.out.println("Some thread was interrupted");
        }
        System.out.println("Count is " + lc.get() + " and should be " + 2 * counts);


    }

    private void incrementTheCount() {
        for (int i = 0; i < counts; i++)
            try {
                lock.lock();
                lc.increment();
            } catch (Exception e) {
                Logger.getAnonymousLogger().log(Level.FINER, "something went wrong " + e.getMessage());
            } finally {
                lock.unlock();
            }
    }

    public static void main(String[] args) {

        new TestLongCounterExperiments();
    }

    class LongCounter {
        private long count = 0;

        public void increment() {
            count = count + 1;
        }

        public long get() {
            return count;
        }
    }
}
