package exercises04.second;

public class Main {

    public static void main(String args[]) throws InterruptedException {
//        Person p = new Person(100);
        Thread t = new Thread(() -> {
            for (int j = 0; j < 10_000; j++) {
                new Person();
            }
        });
        Thread t2 = new Thread(() -> {
            for (int j = 0; j < 10_000; j++) {
                new Person();
            }
        });
        t.start();
        t2.start();
        t.join();
        t2.join();
        // the id must be 20_000
        System.out.println(PersonIdGenerator.getNextAvailableId());

    }
}
