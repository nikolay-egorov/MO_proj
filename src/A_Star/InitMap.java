package A_Star;

import javafx.util.Pair;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class InitMap {
    public SquaredGraph initMap(int demension, ArrayList<Pair<Point2D, Float>> noise_arr){
        SquaredGraph graph = new SquaredGraph(demension);
        for(int i=0; i<demension; i++){
            for(int j=0; j<demension; j++){
                if(i==0&&j==0){
                    Node n = new Node(i,j, "NORMAL");
                    graph.setMapCell(new Point(i,j), n);
                    graph.setStartPosition(new Point(i,j));
                }
                else {
                    Node n = new Node(i, j, "NORMAL");
                    graph.setMapCell(new Point(i, j), n);
                }
                if(i==demension-1&&j==demension-1){
                    Node n = new Node(i,j, "NORMAL");
                    graph.setMapCell(new Point(i,j), n);
                    graph.setTargetPosition(new Point(i,j));
                }
            }
        }
/*        for (Pair<Point2D.Float, Float> aNoise_arr : noise_arr) {
            for (float i = -180; i <= 180; i+=0.5) {
                int r = (int) (aNoise_arr.getValue() * demension);
                int x = (int) (aNoise_arr.getKey().x * demension);
                int y = (int) (aNoise_arr.getKey().y * demension);
                int x_t = (int) (r * cos(i) + x);
                int y_t = (int) (r * sin(i) + y);
                if (y_t < demension & y_t > 0 && x_t > 0 && x_t < demension) {
                    Node n = new Node(x_t, y_t, "OBSTACLE");
                    graph.setMapCell(new Point(x_t, y_t), n);
                }
            }

        }*/
        for (Pair<Point2D, Float> aNoise_arr : noise_arr) {
            int r = (int) ((aNoise_arr.getValue()+0.01f) * demension);
            int x = (int) (aNoise_arr.getKey().getX() * demension);
            int y = (int) (aNoise_arr.getKey().getY() * demension);
            for (int i = x - r; i <= x + r; i++) {
                for (int j = y - r; j <= y + r; j++) {
                    if (((i - x) * (i - x) + (j - y) * (j - y) <= r * r ) && (i < demension && i > 0 && j > 0 && j < demension)) {
                        Node n = new Node(j, i, "OBSTACLE");
                        graph.setMapCell(new Point(j,i), n);
                    }
                }
            }
        }

        return graph;
    }
}
