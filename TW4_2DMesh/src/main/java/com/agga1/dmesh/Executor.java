package com.agga1.dmesh;

import com.agga1.dmesh.mesh.GraphDrawer;
import com.agga1.dmesh.myProductions.PJW;
import com.agga1.dmesh.myProductions.PS;
import com.agga1.dmesh.parallelism.BlockRunner;
import com.agga1.dmesh.myProductions.PW;
import com.agga1.dmesh.myProductions.PI;
import com.agga1.dmesh.mesh.Vertex;
import com.agga1.dmesh.production.PDrawer;

public class Executor extends Thread {
    
    private final BlockRunner runner;
    
    public Executor(BlockRunner _runner){
        this.runner = _runner;
    }

    @Override
    public void run() {

        PDrawer<Vertex> drawer = new GraphDrawer();
        //axiom
        Vertex s = new Vertex("S");

        //p1
        PI p00 = new PI(s, drawer);
        this.runner.addThread(p00);

        //start threads
        this.runner.startAll();
        drawer.draw(p00.getObj());

        PW pw01 = new PW(p00.getObj(), drawer);
        PS ps10 = new PS(p00.getObj(), drawer);
        this.runner.addThread(pw01);
        this.runner.addThread(ps10);
        this.runner.startAll();
        drawer.draw(p00.getObj());

        PW pw02 = new PW(pw01.getObj(), drawer);
        PS ps11 = new PS(pw01.getObj(), drawer);
        PS ps20 = new PS(ps10.getObj(), drawer);
        this.runner.addThread(pw02);
        this.runner.addThread(ps11);
        this.runner.addThread(ps20);
        this.runner.startAll();
        drawer.draw(p00.getObj());

        PS ps12 = new PS(pw02.getObj(), drawer);
        PS ps21 = new PS(ps11.getObj(), drawer);
        this.runner.addThread(ps12);
        this.runner.addThread(ps21);
        this.runner.startAll();

        PS ps22 = new PS(ps12.getObj(), drawer);
        this.runner.addThread(ps22);
        this.runner.startAll();

        PJW pj4 = new PJW(ps21.getObj(), drawer);
        PJW pj2 = new PJW(ps11.getObj(), drawer);
        PJW pj3 = new PJW(ps20.getObj(), drawer);
        PJW pj1 = new PJW(ps10.getObj(), drawer);
        this.runner.addThread(pj1);
        this.runner.addThread(pj2);
        this.runner.addThread(pj3);
        this.runner.addThread(pj4);
        this.runner.startAll();

        System.out.println("done");
        drawer.draw(p00.getObj());

    }
}
