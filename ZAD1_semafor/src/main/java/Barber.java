public class Barber {
    private Semaphore semaphore;
    private int activeAtTheTime=0;
    public Barber(int seats){
        semaphore = new Semaphore(seats);
    }
    public void getHaircut(int timeMillis){
        semaphore.P();
        activeAtTheTime ++;
        System.out.println("active clients: "+activeAtTheTime);
        try {
            Thread.sleep(timeMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        activeAtTheTime --;
        semaphore.V();
        System.out.println("active clients: "+activeAtTheTime);
    }
}
