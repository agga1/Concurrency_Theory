public class BinarySemaphore {
    private boolean _state = true;
    private int _waits = 0;

    public synchronized void P(){
        _waits += 1;
        while(!_state){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        _state = false;
        _waits -=1;
    }
    public synchronized void V(){
        if(!_state){
            _state = true;
            notifyAll();
        }

    }


}
