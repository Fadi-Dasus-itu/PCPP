package third;

public class Histogram2 implements Histogram {
//    the final Guarantees visibility and Safe initialization the array
    private final int[] counts;
    private volatile int total = 0;
    private final Object LOCK = new Object();

    public Histogram2(int span) {
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
