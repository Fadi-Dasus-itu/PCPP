package exercises09.first;

import java.util.concurrent.TimeUnit;
import javax.swing.*;

public class StopwatchN {

    public static void main(String[] args) {
        new StopwatchN();
    }

    public StopwatchN() {
        int n = 10;
        final JFrame[] f = new JFrame[n];
        // final JFrame f = new JFrame("Stopwatch");
        final stopwatchUI[] myUI = new stopwatchUI[n];
        // final stopwatchUI myUI = new stopwatchUI(0, f);

        for (int i = 0; i < n; i++) {
            f[i] = new JFrame("Stopwatch "+ (i+1));
            myUI[i] = new stopwatchUI(0, f[i]);

            f[i].setBounds(0, 0, 220, 220);
            f[i].setLayout(null);
            f[i].setVisible(true);
            final int j = i;
            new Thread() {
                private int seconds = 0;
    
                @Override
                public void run() {
                    try {
                        while (true) {
                            TimeUnit.SECONDS.sleep(1);
                            myUI[j].updateTime();
                        }
                    } catch (InterruptedException e) {
                        System.out.println(e.toString());
                    }
                }
            }.start();
        }

      
       
    }

}
