import java.util.Random;

public class Task3_1 {
    public static void main(String[] args){
        countingSemaphoreExample();
    }

    public static void countingSemaphoreExample(){
        final int nr_clients = 10;
        Barber barber = new Barber(3);
        for(int i =0; i<nr_clients;i++){
            new Thread(() -> {
                try {
                    barber.getHaircut(new Random().nextInt(2000)+500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
