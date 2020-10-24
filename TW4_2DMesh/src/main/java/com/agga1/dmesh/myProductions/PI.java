package com.agga1.dmesh.myProductions;

import com.agga1.dmesh.production.AbstractProduction;
import com.agga1.dmesh.mesh.Vertex;
import com.agga1.dmesh.production.PDrawer;

public class PI extends AbstractProduction<Vertex> {

    public PI(Vertex _obj, PDrawer<Vertex> _drawer) {
        super(_obj, _drawer);
    }

    @Override
    public Vertex apply(Vertex s) {
        System.out.print("PI");
        return new Vertex(null, null, null, null, "M");
    }
}
