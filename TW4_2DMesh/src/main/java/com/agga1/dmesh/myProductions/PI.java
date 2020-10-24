package com.agga1.dmesh.myProductions;

import com.agga1.dmesh.production.AbstractProduction;
import com.agga1.dmesh.mesh.Vertex;
import com.agga1.dmesh.production.PDrawer;

public class PI extends AbstractProduction<Vertex> {

    public PI(Vertex _obj) {
        super(_obj);
    }

    @Override
    public Vertex apply(Vertex s) {
        System.out.print("PI");
        s.setLabel("M");
        return s;
    }
}
