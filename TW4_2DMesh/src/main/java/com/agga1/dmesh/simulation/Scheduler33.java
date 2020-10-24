package com.agga1.dmesh.simulation;
import com.agga1.dmesh.myProductions.PJW;
import com.agga1.dmesh.myProductions.PS;
import com.agga1.dmesh.myProductions.PW;
import com.agga1.dmesh.myProductions.PI;
import com.agga1.dmesh.mesh.Vertex;

public class Scheduler33 extends Thread {
    private final Coordinator coord;
    
    public Scheduler33(){
        this.coord = new Coordinator();
    }

    @Override
    public void run() {
        Vertex S = coord.getAxiom();
        PI p00 = coord.add(new PI(S));
        coord.execute();

        PW pw01 = coord.add( new PW(p00.getObj()));
        PS ps10 = coord.add( new PS(p00.getObj()));
        coord.execute();

        PW pw02 = coord.add( new PW(pw01.getObj()));
        PS ps11 = coord.add( new PS(pw01.getObj()));
        PS ps20 = coord.add( new PS(ps10.getObj()));
        coord.execute();

        PJW pj1 = coord.add( new PJW(ps10.getObj()));
        PS ps12 = coord.add( new PS(pw02.getObj()));
        PS ps21 = coord.add( new PS(ps11.getObj()));
        coord.execute();

        PJW pj2 = coord.add( new PJW(ps11.getObj()));
        PJW pj3 = coord.add( new PJW(ps20.getObj()));
        PS ps22 = coord.add( new PS(ps12.getObj()));
        coord.execute();

        PJW pj4 = coord.add( new PJW(ps21.getObj()));
        coord.execute();

        coord.displayResults();
    }
}
