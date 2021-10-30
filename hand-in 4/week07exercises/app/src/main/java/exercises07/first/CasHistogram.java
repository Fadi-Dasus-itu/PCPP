package exercises07.first;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class CasHistogram implements Histogram {
    private final AtomicInteger[] counts;
    private AtomicInteger total = new AtomicInteger(0);

    public CasHistogram(int span) {
        this.counts = new AtomicInteger[span];
        for (int i = 0; i < span; i++) {
            counts[i] = new AtomicInteger(0);
        }
    }

    // Thread safe since the increment happens through CAS non-blocking operation by verifying that we have an updated version of the variable before updating it
    public void increment(int bin) {
        int oldValue, newValue;
        do {
            oldValue = counts[bin].get();
            newValue = counts[bin].get() + 1;
        } while (!counts[bin].compareAndSet(oldValue, newValue));

        // another way of doing the same update for the total variable
        boolean isTotalSuccessful = false;
        while (!isTotalSuccessful) {
            var value = this.total.get();
            var newValue2 = value + 1;
            isTotalSuccessful = this.total.compareAndSet(value, newValue2);
        }


    }

    // safe since the array is final and visible to other threads
    public int getCount(int bin) {
        return counts[bin].get();
    }

    // safe since the array is final and visible to other threads
    public int getSpan() {
        return counts.length;
    }

    @Override
    public int getAndClear(int bin) {
        int oldValue, newValue;
        do {
            oldValue = counts[bin].get();
            newValue = 0;
        } while (!counts[bin].compareAndSet(oldValue, newValue));

        return oldValue;
    }

    public int getTotal() {
        return total.get();
    }

}
