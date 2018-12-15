package A_Star;

import java.awt.Point;
import java.io.*;
public class InputHandler {

    public SquaredGraph readMap(String filename) throws IOException, InvalidCharException {

        File file = new File(filename);
        BufferedReader in = new BufferedReader(new FileReader(filename));;


        try{

            String dimension =in.readLine();
            int mapDimension = Integer.parseInt(dimension);

            SquaredGraph graph = new SquaredGraph(mapDimension);

            String line;
            for(int i=0; i<mapDimension; i++){
                line = in.readLine();
                for(int j=0; j<mapDimension; j++){
                    char typeSymbol = line.charAt(j);
                    if(typeSymbol == ' '){
                        Node n = new Node(i,j, "NORMAL");
                        graph.setMapCell(new Point(i,j), n);
                    }
                    else if(typeSymbol == 'X'){
                        Node n = new Node(i,j, "OBSTACLE");
                        graph.setMapCell(new Point(i,j), n);
                    }
                    else if(typeSymbol == 'T'){
                        Node n = new Node(i,j, "NORMAL");
                        graph.setMapCell(new Point(i,j), n);
                        graph.setStartPosition(new Point(i,j));
                    }
                    else if(typeSymbol == 'G'){
                        Node n = new Node(i,j, "NORMAL");
                        graph.setMapCell(new Point(i,j), n);
                        graph.setTargetPosition(new Point(i,j));
                    }
                    else{
                        throw new InvalidCharException("There was a wrong character in the text file.The character must be X, ,T or G.");
                    }
                }
            }
            return graph;
        }
        catch(IOException e){
            throw e;
        }
        finally{
            in.close();
        }
    }
}