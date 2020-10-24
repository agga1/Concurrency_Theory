package com.agga1.dmesh;

import com.agga1.dmesh.simulation.Scheduler33;
import com.agga1.dmesh.simulation.SchedulerN;

class Application {

    public static void main(String args[]) {
//        Scheduler33 s = new Scheduler33();
        SchedulerN s = new SchedulerN(3, 3);
        s.start();
    }
}
