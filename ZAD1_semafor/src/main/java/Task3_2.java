import static java.lang.Thread.sleep;

public class Task3_2 {
    public static void main(String[] args) throws InterruptedException {
        final int nr_iter = 1000;
        final int nr_threads = 8;

        Counter counter = new Counter(new Semaphore(1));
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
        sleep(1000);
        System.out.println(counter.getValue());
        assert counter.getValue() == 0 : "counter is not threadsafe!";

    }
}


