package exercises04.third;

public class Main {
    // the idea here is: we want all the addition operation to happen first then the multiplication.
    public static void main(String args[]) throws InterruptedException  {
        Calculator[] calculators = new Calculator[10];
        for (int i=0; i<calculators.length/2; i++) {
            calculators[2*i] = new Calculator("Add-"+i);
            calculators[2*i+1] = new Calculator("Multiply-"+i);
        }
        for (Calculator s : calculators)
            s.start();
        for (Calculator s : calculators)
            s.join();
    }
}
