package exercises04.first;

import java.util.concurrent.Semaphore;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Semaphore binarySemaphore = new Semaphore(1);
        Semaphore isFull = new Semaphore(1);
        Semaphore isEmpty = new Semaphore(10);
        SemaphoreBuffer buffer = new SemaphoreBuffer(10, isFull, isEmpty, binarySemaphore);
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {

                try {
                    buffer.produce();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    buffer.consume();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        t2.start();
        t2.join();
        t1.join();

    }
}
