package third;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class CountPrimes {
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
        final Histogram2 histogram = new Histogram2(30);
        var numberOfProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService pool = Executors.newFixedThreadPool(numberOfProcessors);
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

    public static String performCounting(int from, int to, Histogram2 histogram) {
        for (int i = from; i < to; i++) {
            var result = primeFactors(i);
            histogram.increment(result);
        }
        return "done";
    }

    public static void dump(Histogram histogram) {
        for (int bin = 0; bin < histogram.getSpan(); bin++) {
            System.out.printf("%4d: %9d%n", bin, histogram.getCount(bin));
        }
        System.out.printf("      %9d%n", histogram.getTotal());
    }

}
