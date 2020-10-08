import static java.lang.Thread.sleep;

public class Task2 {
    private static final int nr_iter = 10000;
    private static final int nr_threads = 8;

    public static void main(String[] args) throws InterruptedException {

        System.out.println("race problem using binary semaphores with 'if' ");
        Counter counterIf1 = new Counter(new BinarySemaphoreIf());
        Counter counterIf2 = new Counter(new BinarySemaphoreIf());
        runCounters(counterIf1, counterIf2);

        System.out.println("race problem using binary semaphores with 'while'");
        Counter counter1 = new Counter(new BinarySemaphore());
        Counter counter2 = new Counter(new BinarySemaphore());
        runCounters(counter1, counter2);
    }
    public static void runCounters(Counter counter1, Counter counter2) throws InterruptedException {

        for(int i =0; i<nr_threads/2;i++){
            new Thread(() -> {
                for (int j = 0 ; j < nr_iter; j++) {
                    counter1.increment();
                    counter2.increment();
                }
            }).start();
        }
        for(int i =nr_threads/2; i<nr_threads;i++){
            new Thread(() -> {
                for (int j = 0 ; j < nr_iter; j++){
                    counter1.decrement();
                    counter2.decrement();
                }
            }).start();
        }
        sleep(1200);
        System.out.println("counter1: "+counter1.getValue());
        System.out.println("counter2: "+counter2.getValue());
    }
}
