public class Counter {
    private int value = 0;
    BinarySemaphore binSemaphore = new BinarySemaphore();

    public void increment() {
        binSemaphore.P();
        this.value += 1;
        binSemaphore.V();
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


