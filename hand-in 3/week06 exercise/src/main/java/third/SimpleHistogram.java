package third;

public class SimpleHistogram {
    public static void main(String[] args) {
        final Histogram histogram = new Histogram1(30);
        histogram.increment(7);
        histogram.increment(13);
        histogram.increment(7);
        dump(histogram);
    }

    public static void dump(Histogram histogram) {
        for (int bin = 0; bin < histogram.getSpan(); bin++) {
            System.out.printf("%4d: %9d%n", bin, histogram.getCount(bin));
        }
        System.out.printf("      %9d%n", histogram.getTotal());
    }
}
