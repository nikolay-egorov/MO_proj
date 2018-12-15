package A_Star;

import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import A_Star.*;
import javafx.util.Pair;

public class Main {

    public static void main(String[] args) throws InvalidCharException, FileNotFoundException, IOException,HeapException{

//        if(args.length != 1){
//            System.out.println("Usage: java A_StarAlgorithm <filename>");
//        }
//        else{
//            String filename = args[0];
            String filename="in.txt";

            InputHandler handler = new InputHandler();
//            SquaredGraph graph = handler.readMap(filename);
            InitMap initMap=new InitMap();

            int ind=0;
            ArrayList<Pair<Point2D, Float>> noise_arr = new ArrayList<>();
            noise_arr.add(ind++, new Pair<>(new Point2D.Float(0.5f, 0.5f), 0.3f));
            noise_arr.add(ind++, new Pair<>(new Point2D.Float(0.22f, 0.3f), 0.1f));
            noise_arr.add(ind++, new Pair<>(new Point2D.Float(0.02f, 0.11f), 0.01f));

            SquaredGraph graph = initMap.initMap(100,noise_arr);
            ArrayList<Node> path = graph.executeAStar();

            if(path == null){
                System.out.println("There is no path to target");
            }
            else{
                System.out.println("The total number of moves from distance to the target are : " + path.size());
                System.out.println("You want to see the whole path to the target ? (y/n) ");
                Scanner scanner = new Scanner(System.in);
                String response = scanner.nextLine();
                if(response.equals("y")){
                    System.out.println("--- Path to target ---");
//                    graph.printPath(path);
                    graph.printMap(path);
                }
            }
 //       }
    }

}

