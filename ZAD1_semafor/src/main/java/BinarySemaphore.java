public class BinarySemaphore implements ISemaphore{
    private boolean _state = true;

    public synchronized void P(){
        while(!_state){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        _state = false;
    }
    public synchronized void V(){
        if(!_state){
            _state = true;
            notifyAll();
        }
    }


}
