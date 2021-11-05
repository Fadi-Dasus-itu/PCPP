package exercises09.first;

import java.util.concurrent.TimeUnit;
import javax.swing.*;

class Stopwatch2 {

    public static void main(String[] args) {
        new Stopwatch2();
    }

    public Stopwatch2() {

        final JFrame f1 = new JFrame("Stopwatch2");
        final JFrame f2 = new JFrame("Stopwatch2");
        final stopwatchUI myUI1 = new stopwatchUI(0, f1);
        f1.setBounds(0, 0, 420, 420);
        f1.setLayout(null);
        f1.setVisible(true);

        final stopwatchUI myUI2 = new stopwatchUI(0, f2);
        f2.setBounds(0, 0, 420, 420);
        f2.setLayout(null);
        f2.setVisible(true);

        // Background Thread simulating a clock ticking every 1 seconde
        new Thread() {
            private int seconds = 0;

            @Override
            public void run() {
                try {
                    while (true) {
                        TimeUnit.SECONDS.sleep(1);
                        myUI1.updateTime();
                    }
                } catch (InterruptedException e) {
                    System.out.println(e.toString());
                }
            }
        }.start();

        new Thread() {
            private int seconds = 0;

            @Override
            public void run() {
                try {
                    while (true) {
                        TimeUnit.SECONDS.sleep(1);
                        myUI2.updateTime();
                    }
                } catch (InterruptedException e) {
                    System.out.println(e.toString());
                }
            }
        }.start();
    }
}
