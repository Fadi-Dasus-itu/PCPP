package third;

interface Histogram {
    public void increment(int bin);
    public int getCount(int bin);
    public float getPercentage(int bin);
    public int getSpan();
    public int getTotal();
}
