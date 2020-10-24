package com.agga1.dmesh.myProductions;

import com.agga1.dmesh.mesh.Vertex;
import com.agga1.dmesh.production.AbstractProduction;
import com.agga1.dmesh.production.PDrawer;

public class PJW extends AbstractProduction<Vertex> {
    /* Joins west by north-west-south connection*/

    public PJW(Vertex _obj) {
        super(_obj);
    }

    @Override
    public Vertex apply(Vertex t) {
        System.out.print("->PJW");
        Vertex t1 = t.getNorth().getWest().getSouth();
        t.setWest(t1);
        t1.setEast(t);
        return t;
    }
}
