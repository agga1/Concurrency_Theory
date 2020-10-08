import java.util.Random;

import static java.lang.Thread.sleep;

public class Task3 {
    public static void main(String[] args){
        final int nr_clients = 20;
        Barber barber = new Barber(3);
        for(int i =0; i<nr_clients;i++){
            new Thread(() -> barber.getHaircut(new Random().nextInt(2000)+500)).start();
        }
    }
}
