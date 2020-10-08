public class Semaphore implements ISemaphore{
    private final BinarySemaphore countAndNewSlotSem = new BinarySemaphore();
    private final BinarySemaphore sectionSem = new BinarySemaphore();
    private int count;
    private boolean newSlotFreed = false;

    public Semaphore(int count){
        sectionSem.P();
        this.count = count;
    }

    public void P(){
        countAndNewSlotSem.P();
        count -= 1;
        System.out.println("count: "+count);
        if(count < 0) {
            while (!newSlotFreed) {
                countAndNewSlotSem.V();
                sectionSem.P();
            }
            newSlotFreed = false;
        }
        countAndNewSlotSem.V();
    }

    public void V(){
        countAndNewSlotSem.P();
        count += 1;
        System.out.println("new count: "+count);
        if(count <= 0){
            newSlotFreed = true;
            sectionSem.V();
        }else {
            countAndNewSlotSem.V();
        }
    }

    public int getCount(){
        return count;
    }

}
