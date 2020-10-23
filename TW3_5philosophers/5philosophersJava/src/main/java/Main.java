import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args){
        runWithArbiter(3, 10000);
//        runWithStarve(6, 10000);
    }
    public static void runWithArbiter(int n, int howLongMillis){
        System.out.println();
        PhilosopherWithArbiter[] philosophers = createPhilosophersWithArbiter(n);
        runPhilosophers(philosophers, howLongMillis);
    }
    public static void runWithStarve(int n, int howLongMillis){
        PhilosopherWithStarve[] philosophers = createPhilosophersWithStarve(n);
        runPhilosophers(philosophers, howLongMillis);
    }
    public static PhilosopherWithArbiter[] createPhilosophersWithArbiter(int n){
        Semaphore[] sticks = createSticks(n);
        Semaphore atTheTable = new Semaphore(n-1);
        PhilosopherWithArbiter[] philosophers = new PhilosopherWithArbiter[n];
        for(int i=0; i<n; i++){
            philosophers[i] = new PhilosopherWithArbiter(atTheTable, sticks[i], sticks[(i+1)%n]);
        }
        return philosophers;
    }
    public static PhilosopherWithStarve[] createPhilosophersWithStarve(int n){
        Semaphore[] sticks = createSticks(n);
        PhilosopherWithStarve[] philosophers = new PhilosopherWithStarve[n];
        for(int i=0; i<n; i++){
            philosophers[i] = new PhilosopherWithStarve(sticks[i], sticks[(i+1)%n]);
        }
        return philosophers;
    }
    public static Semaphore[] createSticks(int n){
        Semaphore[] sticks = new Semaphore[n];
        for(int i=0;i<n;i++){
            sticks[i] = new Semaphore(1);
        }
        return sticks;
    }
    // runs all philosophers and displays average wait time for each of them every howLongMillis milliseconds
    public static void runPhilosophers(Philosopher[] philosophers, long howLongMillis){
        for (Philosopher philosopher : philosophers) {
            philosopher.start();
        }
        try {
            while(true){
                sleep(howLongMillis);
                System.out.println(philosophers.length);
                long sum = 0;
                for (Philosopher philosopher : philosophers) {
                    long wait = philosopher.getAverageWait();
                    System.out.println(wait);
                    sum += wait;
                }
                System.out.println("average: "+sum/philosophers.length);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
