// For week 3
// sestoft@itu.dk * 2014-09-04
// thdy@itu.dk * 2019
// kasper@itu.dk * 2020
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

interface Histogram {
  public void increment(int bin);
  public int getCount(int bin);
  public float getPercentage(int bin);
  public int getSpan();
  public int getTotal();
}

public class SimpleHistogram {
  public static void main(String[] args) {
    final Histogram histogram = new Histogram1(30);
    final Histogram2 histogram2 = new Histogram2(49);
    histogram2.increment(49);
    
    dump(histogram2);
}

  public synchronized static void dump(Histogram2 histogram) {
    for (int bin = 0; bin < histogram.getSpan(); bin++) {
      System.out.println(bin+": "+ histogram.getCount(bin));
    }
    
  }
}


// Added a copy of Histogram to make it thread safe..
class Histogram2 implements Histogram {
  private int[] counts;
  private int total=0;

  public Histogram2(int span) {
    this.counts = new int[span];
    
  }

  public synchronized void increment(int span) {
    ExecutorService exec = Executors.newFixedThreadPool(8);
    
      Runnable task = new Runnable() {
        public void run() {
          for(int i=0; i<=span; i++){
            for (int j=0; j<= i; j++)
              if (isPrime(j))
                counts[j] = counts[j] + 1;
                total++;
          }
        }
      };exec.execute(task); 

    exec.shutdown(); 
    
    
  }

  public synchronized int getCount(int bin) {
    return counts[bin];
  }
    
  public float getPercentage(int bin){
    return getCount(bin) / getTotal() * 100;
  }

  public synchronized int getSpan() {
    return counts.length;
  }
    
  public synchronized int getTotal(){
    return total;
  }

  private static boolean isPrime(int n) {
    int k = 2;
    while (k * k <= n && n % k != 0)
      k++;
    return n >= 2 && k * k > n;
  }
   

}


