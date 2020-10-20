import java.util.concurrent.Semaphore;

public class PhilosopherWithArbiter extends Philosopher{
    private final Semaphore atTheTable;
    private final Semaphore left;
    private final Semaphore right;

    public PhilosopherWithArbiter(Semaphore atTheTable, Semaphore left, Semaphore right){
        this.atTheTable = atTheTable;
        this.left = left;
        this.right = right;
    }

    @Override
    void acquireForks() {
        try {
            atTheTable.acquire();
            left.acquire();
            right.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    void releaseForks() {
        left.release();
        right.release();
        atTheTable.release();
    }

}

