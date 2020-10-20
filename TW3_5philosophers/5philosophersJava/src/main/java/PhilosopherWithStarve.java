import java.util.concurrent.Semaphore;

public class PhilosopherWithStarve extends Philosopher{
    private final Semaphore left;
    private final Semaphore right;

    public PhilosopherWithStarve(Semaphore left, Semaphore right){
        this.left = left;
        this.right = right;
    }

    @Override
    void acquireForks() {
        boolean gotRight=false;
        while(!gotRight){
            boolean gotLeft = left.tryAcquire();
            if(gotLeft) gotRight = right.tryAcquire();
            if(!gotRight && gotLeft) left.release();
        }
    }

    @Override
    void releaseForks() {
        left.release();
        right.release();
    }
}

