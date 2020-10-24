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

public class SchedulerN extends Thread {
    private final Coordinator coordinator;
    private final int n;
    private final int m;
    Deque<AbstractProduction<Vertex>> previous = new LinkedList<>();
    Deque<AbstractProduction<Vertex>> current = new LinkedList<>();
    Queue<Vertex> toJoin = new LinkedList<>();
    Vertex firstFromPrevious;

    public SchedulerN(int n, int m){
        this.n= n;
        this.m= m;
        this.coordinator = new Coordinator();
    }

    @Override
    public void run() {
        addInsertProduction();
        coordinator.executeAndDraw();
        for(int i=0;i<m+n-1;i++){
            current = new LinkedList<>();
            addJoinProductions();
            addWestProductions(i);
            addSouthProductions(i);
            previous = current;
            coordinator.executeAndDraw();
        }
        coordinator.displayResults();
    }

    private void addInsertProduction(){
        Vertex S = coordinator.getAxiom();
        PI p00 = coordinator.add(new PI(S));
        previous.add(p00);
    }

    private void addJoinProductions(){
        while(!toJoin.isEmpty()) coordinator.add(new PJW(toJoin.remove()));
    }

    private void addWestProductions(int i){
        firstFromPrevious = previous.removeFirst().getObj();
        if(i<m-1) {
            PW pw = coordinator.add(new PW(firstFromPrevious));
            current.add(pw);
        }
    }

    private void addSouthProductions(int i){
        PS ps = new PS(firstFromPrevious);
        while(!previous.isEmpty()){
            coordinator.add(ps);
            current.add(ps);
            Vertex v = previous.removeFirst().getObj();
            toJoin.add(v);
            ps = new PS(v);
        }
        if(i<n-1){
            coordinator.add(ps);
            current.add(ps);
        }
    }
}
