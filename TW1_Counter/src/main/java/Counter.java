public class Counter implements ICounter{
    private int value = 0;

    public void increment(){
        this.value += 1;
    }

    public void decrement(){
        this.value -= 1;
    }

    public int getValue() {
        return value;
    }
}

