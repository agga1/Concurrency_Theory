package com.agga1.dmesh.mesh;
import com.agga1.dmesh.production.PDrawer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import static java.lang.Math.max;

public class GraphDrawer implements PDrawer<Vertex> {
    
    private String[][] traverse(Vertex v){
        HashMap<Vertex, int[]> vMap = new HashMap<>();
        Queue<Vertex> q = new LinkedList<>();
        int width = 1, height = 1;
        vMap.put(v, new int[]{1, 1});
        q.add(v);
        while(!q.isEmpty()){
            v = q.remove();
            int[] offset = vMap.get(v);
            if(v.getWest() != null){
                Vertex tmp = v.getWest();
                if(!vMap.containsKey(tmp)){
                    vMap.put(tmp, new int[]{offset[0], offset[1]+2});
                    width = max(width, offset[1]+2);
                    q.add(tmp);
                }
            }
            if(v.getSouth() != null){
                Vertex tmp = v.getSouth();
                if(!vMap.containsKey(tmp)){
                    vMap.put(tmp, new int[]{offset[0]+2, offset[1]});
                    height = max(height, offset[0]+2);
                    q.add(tmp);
                }
            }
        }
        String[][] matrix = new String[height][width];
        for(Vertex vertex: vMap.keySet()) {
            int x = vMap.get(vertex)[0]-1;
            int y = vMap.get(vertex)[1]-1;
            matrix[x][y] = v.getLabel();
            if(vertex.getWest() != null) matrix[x][y+1] = "-";
            if(vertex.getSouth() != null) matrix[x+1][y] = "|";
        }
        return matrix;
    }

    @Override
    public void draw(Vertex v) {
        System.out.println();
        String[][] m = traverse(v);
        for (String[] row : m) {
            for(int i = row.length-1;i>=0;i--)
                System.out.print(row[i] != null ? row[i] : " ");
            System.out.println();
        }
    }
}
