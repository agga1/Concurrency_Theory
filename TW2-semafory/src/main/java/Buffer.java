import static java.lang.Thread.sleep;

public class Buffer {
    private static final int capacity = 10;
    private final int[] values = new int[capacity];
    private int freeSlots = capacity;
    private int putIt = 0;
    private int getIt = 0;

    public synchronized void put(int i) {
        while(this.isFull()){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        values[putIt] = i;
        freeSlots --;
        putIt = (putIt + 1)%capacity;
        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        notify();
    }

    public synchronized int get() {
        while (this.isEmpty()){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int value = values[getIt];
        freeSlots ++;
        getIt = (getIt + 1)%capacity;
        notify();
        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return value;
    }

    private boolean isEmpty(){
        return freeSlots == capacity;
    }
    private boolean isFull(){
        return freeSlots == 0;
    }
}
