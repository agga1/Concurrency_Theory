import static java.lang.Thread.sleep;

public class Task1 {
    public static void main(String[] args) throws InterruptedException {
        final int nr_iter = 10000;
        final int nr_threads = 10;
        Counter counter = new Counter();
        for(int i =0; i<nr_threads/2;i++){
            new Thread(() -> {
                for (int j = 0 ; j < nr_iter; j++)
                    counter.increment();
            }).start();
        }
        for(int i =nr_threads/2; i<nr_threads;i++){
            new Thread(() -> {
                for (int j = 0 ; j < nr_iter; j++)
                    counter.decrement();
            }).start();
        }
        sleep(2000);
        System.out.println(counter.getValue());

    }
}
