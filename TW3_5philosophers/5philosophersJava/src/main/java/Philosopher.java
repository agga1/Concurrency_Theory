import java.util.Random;

import static java.lang.Integer.max;

public abstract class Philosopher extends Thread{
    private long waitElapsed=0;
    private int eatCount=0;
    private long start=0;

    abstract void acquireForks();
    abstract void releaseForks();

    private void eat(int timeMillis) throws InterruptedException {
        System.out.println("------"+getId()+" eating");
        sleep(timeMillis);
//        System.out.println("<<"+getId()+" finished");
    }
    private void think(int timeMillis) throws InterruptedException {
        sleep(timeMillis);
    }

    @Override
    public void run(){
        try {
            while(true) {
                start = System.currentTimeMillis();
                acquireForks();
                waitElapsed += getAccumulatedTime();
                eatCount++;
                eat(getRandomBetween(500, 1000));
                releaseForks();
                think(getRandomBetween(0, 10));
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private long getAccumulatedTime(){
        if(start==0) return 0;
        long accumulated =  System.currentTimeMillis() - start;
        start = 0;
        return accumulated;
    }
    static int getRandomBetween(int from, int to){
        return new Random().nextInt(to-from)+from;
    }
    public long getAverageWait() {
        if(start != 0) {
            waitElapsed += getAccumulatedTime();
            start = System.currentTimeMillis();
        }
        return waitElapsed/max(eatCount, 1);
    }
}
