package exercises04.third;


import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 */
public class Calculator extends Thread {

    private static Semaphore calculator = new Semaphore(10);
    private static Lock LOCK = new ReentrantLock();
    private static CyclicBarrier fistBump = new CyclicBarrier(10);

    private static int result = 0;

    public Calculator(String name) {
        this.setName(name);
    }

    public synchronized void addTwo() {
        synchronized (LOCK) {
            result += 2;
        }
    }

    public synchronized void multiplyByTwo() {
        synchronized (LOCK) {
            result *= 2;
        }
    }

    public synchronized int getResult() {
        synchronized (LOCK) {
            return this.result;
        }
    }

    public void run() {
        if (this.getName().contains("Add") && calculator.availablePermits() != 0) {
            doAddition();
        } else if (this.getName().contains("Multiply") && calculator.availablePermits() != 0) {
            doMultiplication();
        }
    }

    private void doMultiplication() {
        try {
            fistBump.await();
            calculator.acquire();
            multiplyByTwo();
            System.out.println(this.getName() + " DOUBLED the result.");
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        } finally {
            calculator.release();
        }
    }

    private void doAddition() {
        try {
            calculator.acquire();
            addTwo();
            System.out.println(this.getName() + " ADDED two to the result.");
            calculator.release();
            fistBump.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        } finally {
            calculator.release();
        }
    }
}
