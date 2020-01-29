package A_Star;

import javafx.util.Pair;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Finder {
    SquaredGraph graph;
    public Finder( ){

    }

     public ArrayList<Node> findPath(int demension,  ArrayList<Pair<Point2D, Float>> noise_arr){
        InitMap initMap =new InitMap();
        SquaredGraph graph = initMap.initMap(demension,noise_arr);
        ArrayList<Node> path = graph.executeAStar();
//        graph.printMap(path);
        return  path;
    }

}
