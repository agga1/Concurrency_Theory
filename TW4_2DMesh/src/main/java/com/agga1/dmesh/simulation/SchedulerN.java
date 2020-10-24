package com.agga1.dmesh.simulation;

import com.agga1.dmesh.mesh.Vertex;
import com.agga1.dmesh.myProductions.PI;
import com.agga1.dmesh.myProductions.PJW;
import com.agga1.dmesh.myProductions.PS;
import com.agga1.dmesh.myProductions.PW;
import com.agga1.dmesh.production.AbstractProduction;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

import static java.lang.Math.*;

public class SchedulerN extends Thread {
    private final Coordinator coord;
    private final int n;
    private final int m;

    public SchedulerN(int n, int m){
        this.n= n;
        this.m= m;
        this.coord = new Coordinator();
    }

    @Override
    public void run() {
        Vertex S = coord.getAxiom();
        Deque<AbstractProduction<Vertex>> previous = new LinkedList<>();
        Queue<Vertex> toJoin = new LinkedList<>();

        PI p00 = coord.add(new PI(S));
        previous.add(p00);
        coord.execute();

        for(int i=0;i<m+n-1;i++){
            Deque<AbstractProduction<Vertex>> current = new LinkedList<>();
            // PJW
            while(!toJoin.isEmpty()) coord.add(new PJW(toJoin.remove()));
            // PW
            Vertex first = previous.removeFirst().getObj();
            if(i<m-1) {
                PW pw = coord.add(new PW(first));
                current.add(pw);
            }
            // PS
            PS ps = new PS(first);
            while(!previous.isEmpty()){
                coord.add(ps);
                current.add(ps);
                Vertex v = previous.removeFirst().getObj();
                toJoin.add(v);
                ps = new PS(v);
            }
            if(i<n-1){
                coord.add(ps);
                current.add(ps);
            }
            previous = current;
            coord.execute();
        }
        coord.displayResults();
    }
}
