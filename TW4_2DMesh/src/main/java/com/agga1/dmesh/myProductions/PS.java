package com.agga1.dmesh.myProductions;

import com.agga1.dmesh.mesh.Vertex;
import com.agga1.dmesh.production.AbstractProduction;
import com.agga1.dmesh.production.PDrawer;

public class PS extends AbstractProduction<Vertex> {

    public PS(Vertex _obj, PDrawer<Vertex> _drawer) {
        super(_obj, _drawer);
    }

    @Override
    public Vertex apply(Vertex t) {
        System.out.print("->PS");
        Vertex t1 = new Vertex(t, null, null, null , "M");
        t.setSouth(t1);
        return t1;
    }
}
