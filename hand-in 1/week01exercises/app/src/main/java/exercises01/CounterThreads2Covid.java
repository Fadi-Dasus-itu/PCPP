// For week 1
// raup@itu.dk * 2021-08-27
package exercises01;

import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CounterThreads2Covid {
    long counter = 0;
    final long PEOPLE = 10_000;
    final long MAX_PEOPLE_COVID = 15_000;

    public CounterThreads2Covid() {
        try {
            Turnstile turnstile1 = new Turnstile();
            Turnstile turnstile2 = new Turnstile();

            turnstile1.start();
            turnstile2.start();
            turnstile1.join();
            turnstile2.join();

            System.out.println(counter + " people entered");
        } catch (InterruptedException e) {
            Logger.getAnonymousLogger().log(Level.FINER, "something went wrong " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new CounterThreads2Covid();
    }


    public class Turnstile extends Thread {
        private final ReentrantLock lock = new ReentrantLock();

        public void run() {
            for (int i = 0; i < PEOPLE; i++) {
                if (counter < MAX_PEOPLE_COVID) {
                    try {
                        lock.lock();
                        counter++;
                    } catch (Exception e) {
                        Logger.getAnonymousLogger().log(Level.FINER, "something went wrong " + e.getMessage());
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }
    }
}
