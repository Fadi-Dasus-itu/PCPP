package exercises07.first;

public class HistogramLock implements Histogram2{
    //    the final Guarantees visibility and Safe initialization the array
    private final int[] counts;
    private volatile int total = 0;
    private final Object LOCK = new Object();

    public HistogramLock(int span) {
        this.counts = new int[span];
    }

    public void increment(int bin) {
        synchronized (LOCK) {
            counts[bin] = counts[bin] + 1;
            total++;
        }
    }

    public int getCount(int bin) {
        synchronized (LOCK) {
            return counts[bin];
        }
    }

    public float getPercentage(int bin) {
        synchronized (LOCK) {
            return getCount(bin) / getTotal() * 100;
        }
    }

    public int getSpan() {
        return counts.length;
    }

    public int getTotal() {
        return total;
    }
}


