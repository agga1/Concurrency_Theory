package com.agga1.dmesh.myProductions;

import com.agga1.dmesh.production.AbstractProduction;
import com.agga1.dmesh.production.PDrawer;
import com.agga1.dmesh.mesh.Vertex;

public class PW extends AbstractProduction<Vertex> {

    public PW(Vertex _obj, PDrawer<Vertex> _drawer) {
        super(_obj, _drawer);
    }

    @Override
    public Vertex apply(Vertex t) {
        System.out.print("->PW");
        Vertex t1 = new Vertex(null, t, null, null, "M");
        t.setWest(t1);

        return t1;
    }
}
