public class Main {
    private static void run_unsync(int nr_iter) throws InterruptedException {
        Counter counter = new Counter();

        new Thread(() ->
        {
            for (int i = 0 ; i < nr_iter; i++)
                counter.increment();
        }).start();

        new Thread(() ->
        {
            for (int i = 0 ; i < nr_iter; i++)
                counter.decrement();
        }).start();

        Thread.sleep(1500);
        System.out.println("Unsynchronized counter: " + counter.getValue());
    }

    private static void run_sync(int nr_iter) throws InterruptedException {
        SynchronizedCounter counter = new SynchronizedCounter();

        new Thread(() ->
        {
            for (int i = 0 ; i < nr_iter; i++)
                counter.increment();
        }).start();

        new Thread(() ->
        {
            for (int i = 0 ; i < nr_iter; i++)
                counter.decrement();
        }).start();

        Thread.sleep(1500);
        System.out.println("Synchronized counter: " + counter.getValue());
    }

    public static void main(String[] args) throws InterruptedException
    {
        run_unsync(100000);
        run_sync(100000);
    }
}
