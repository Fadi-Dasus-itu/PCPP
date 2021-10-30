package exercises07.first;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import exercises07.Histogram;

public class Main {
    public static int primeFactors(int n) {
        var count = 0;
        for (int factor = 2; factor * factor <= n; factor++) {
            while (n % factor == 0) {
                count++;
                n = n / factor;
            }
        }
        if (n > 1)
            count++;
        return count;
    }


    public static void main(String[] args) throws InterruptedException, ExecutionException {

        final CasHistogram histogram = new CasHistogram(30);
//        performCounting(0, 5_000_000, histogram);
        ExecutorService pool = Executors.newFixedThreadPool(3);
        Future future = pool.submit(() -> {
            performCounting(0, 2_500_000, histogram);
        });
        Future future2 = pool.submit(() -> {
            performCounting(2_500_000, 4_000_000, histogram);
        });
        Future future3 = pool.submit(() -> {
            performCounting(4_000_000, 5_000_000, histogram);
        });

        future.get();
        future2.get();
        future3.get();
        pool.shutdown();
        dump(histogram);
    }

    public static  String performCounting(int from, int to, CasHistogram histogram) {
        for (int i = from; i < to; i++) {
            var result = primeFactors(i);
            histogram.increment(result);
        }
        return "done";
    }

    public static void dump(CasHistogram histogram) {
        for (int bin = 0; bin < histogram.getSpan(); bin++) {
            System.out.printf("%4d: %9d%n", bin, histogram.getCount(bin));
        }
        System.out.printf("      %9d%n", histogram.getTotal());
    }
}
