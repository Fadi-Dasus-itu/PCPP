package exercises01;

import java.awt.font.FontRenderContext;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

class Printer extends Thread {
    private final ReentrantLock lock = new ReentrantLock();

    public Printer() {
        // is the second thread will ever get running, since we are in a while true loop
    while (true)
        {
            lock.lock();
            System.out.print("-");
            try {
                Thread.sleep(50);
                System.out.print("|");
            } catch (InterruptedException e) {
                Logger.getAnonymousLogger().log(Level.FINER, "something went wrong " + e.getMessage());
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {

        var printer1 = new Printer();
        var printer2 = new Printer();
        printer1.start();
        printer2.start();
        try {
            printer1.join();
            printer2.join();
        } catch (InterruptedException exn) {
            System.out.println("Some thread was interrupted");
        }
    }
}
