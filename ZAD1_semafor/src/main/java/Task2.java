import java.util.LinkedList;
import java.util.Queue;

import static java.lang.Thread.sleep;

public class Task2 {
    private static final int nr_iter = 10000;
    private static final int nr_threads = 8;

    public static void main(String[] args) throws InterruptedException {
        monitorAndWaitRace();

        sleep(1000);
        CountersRace();
    }

    public static void monitorAndWaitRace() throws InterruptedException {
        Queue<Integer> q = new LinkedList<>();
        Producer producer = new Producer(q);
        Consumer consumer = new Consumer(q);
        Consumer consumer1 = new Consumer(q);
        Consumer consumer2 = new Consumer(q);
        consumer.start();
        sleep(50);
        producer.start();
        consumer1.start();
        consumer2.start();
    }

    public static class Producer extends Thread{
        private final Queue<Integer> q;
        public Producer(Queue<Integer> q){ this.q = q; }

        public void run(){
            synchronized (q) {
                System.out.println("putting 1 >>");
                q.add(1);
                q.notify();
            }
        }
    }
    public static class Consumer extends Thread{
        private final Queue<Integer> q;
        public Consumer(Queue<Integer> q){ this.q = q; }

        public void run() {
            try{
                System.out.println(getId()+" waits for monitor");
                synchronized (q) {
                    System.out.println(getId()+" got monitor");
                    if (q.isEmpty())
                        q.wait();
                    System.out.println(getId()+" taking 1");
                    q.remove();
                }
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }


    public static void CountersRace() throws InterruptedException {
        System.out.println("race problem using binary semaphores with 'if' ");
        Counter counterIf1 = new Counter(new BinarySemaphoreIf());
        Counter counterIf2 = new Counter(new BinarySemaphoreIf());
        runCounters(counterIf1, counterIf2);

        System.out.println("... and using binary semaphores with 'while'");
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
