package com.agga1.dmesh.simulation;
import com.agga1.dmesh.mesh.GraphDrawer;
import com.agga1.dmesh.mesh.Vertex;
import com.agga1.dmesh.parallelism.BlockRunner;
import com.agga1.dmesh.parallelism.ConcurentBlockRunner;
import com.agga1.dmesh.production.AbstractProduction;
import com.agga1.dmesh.production.PDrawer;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

public class Coordinator {
    /* Provides axiom S. Coordinates simulation execution and display. */

    private final BlockRunner runner;
    PDrawer<Vertex> drawer = new GraphDrawer();
    private final Vertex S00;

    public Coordinator() {
        this.runner = new ConcurentBlockRunner();
        this.S00 = new Vertex("S");
    }

    public <T extends AbstractProduction<Vertex>> T add(T p){
        this.runner.addThread(p);
        return p;
    }

    public void executeAndDraw(){
        this.runner.startAll();
        drawer.draw(S00);
    }

    public void displayResults(){
        System.out.println("-- Done. Created mesh:");
        drawer.draw(S00);
    }

    public Vertex getAxiom(){
        return S00;
    }
}
