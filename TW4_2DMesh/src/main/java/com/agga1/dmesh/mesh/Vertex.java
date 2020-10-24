package com.agga1.dmesh.mesh;

public class Vertex {
    /* each vertex has unique id */
    static int instCount = 0;
    int mId;

    private String label;
    private Vertex north;
    private Vertex east;
    private Vertex south;
    private Vertex west;

    public Vertex(Vertex _north, Vertex _east, Vertex _south, Vertex _west,  String _label) {
        this.north = _north;
        this.east = _east;
        this.south = _south;
        this.west = _west;
        this.label = _label;
        this.mId = instCount;
        instCount++;
    }
    public Vertex(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Vertex getNorth() {
        return north;
    }

    public void setNorth(Vertex north) {
        this.north = north;
    }

    public Vertex getEast() {
        return east;
    }

    public void setEast(Vertex east) {
        this.east = east;
    }

    public Vertex getSouth() {
        return south;
    }

    public void setSouth(Vertex south) {
        this.south = south;
    }

    public Vertex getWest() {
        return west;
    }

    public void setWest(Vertex west) {
        this.west = west;
    }
}
