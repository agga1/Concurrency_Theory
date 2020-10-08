public class Counter {
    private int value = 0;
    ISemaphore binSemaphore;

    public Counter(ISemaphore binSemaphore){
        this.binSemaphore = binSemaphore;
    }

    public void increment() {
        this.binSemaphore.P();
        this.value += 1;
        this.binSemaphore.V();
    }
    public void decrement() {
        binSemaphore.P();
        this.value -= 1;
        binSemaphore.V();
    }
    public int getValue() {
        return value;
    }

}


