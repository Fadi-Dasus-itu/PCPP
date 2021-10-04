package exercises04.first.version2;

public class MainBuildInQueue {

    public static void main(String[] args) throws InterruptedException {

        BufferBuiltInQueue buildInQueueBuffer = new BufferBuiltInQueue(10);

        var t1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                    buildInQueueBuffer.produce();
            }
        });

        var t2 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                    buildInQueueBuffer.consume();
            }
        });
        t1.start();
        t2.start();
        t2.join();
        t1.join();

    }

}
