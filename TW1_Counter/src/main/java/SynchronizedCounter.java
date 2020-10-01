public class SynchronizedCounter implements ICounter{
    private int value = 0;

    public synchronized void increment(){
        value += 1;
    }

    public synchronized void decrement(){
        value -= 1;
    }

    public int getValue() {
        return value;
    }
}


