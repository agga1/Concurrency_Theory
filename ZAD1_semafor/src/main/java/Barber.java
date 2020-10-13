public class Barber {
    private final Semaphore semaphore;
    private final int seats;
    private int activeAtTheTime=0;

    public Barber(int seats){
        this.seats = seats;
        semaphore = new Semaphore(seats);
    }

    public void getHaircut(int timeMillis) throws InterruptedException {
        semaphore.P();
        activeAtTheTime ++;
        System.out.println("<<");
        assert activeAtTheTime <= seats : "more active clients than available seats!";
        Thread.sleep(timeMillis);
        activeAtTheTime --;
        System.out.println("  >>");
        semaphore.V();
    }
}
