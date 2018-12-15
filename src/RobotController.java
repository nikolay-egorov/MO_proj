import A_Star.Finder;
import A_Star.Node;
import com.sun.javafx.geom.Vec2f;
import javafx.util.Pair;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class RobotController {

    private ArrayList<Node> path;
    private Point2D location;
    private float F_m;
    private float dt;
    private int chromoLength;
    ArrayList<Pair<Point2D, Float>> noise_arr;
    Stack<Point2D> true_path;
    List<Robot> robots;
    List<Robot> elite;
    Finder f;
    protected float FmDt;
    public RobotController(){
        noise_arr=new ArrayList<>();
        robots=new ArrayList<>();
        elite=new ArrayList<>();
    }


    public void true_path( ){
        path.add( new Node(99,99,"NORMAL"));
        path.add( new Node(100,100,"NORMAL"));
        true_path =new Stack<>();
        Collections.reverse(path);
        for (Node i:path){
            Point2D p=new Point2D.Float(i.getY()/100f,i.getX()/100f);
            true_path.add(p);
        }
    }

    public void initFromF(String filename) throws Exception {
        File file = new File(filename);

        try(BufferedReader in = new BufferedReader(new FileReader(filename))){
            String forceNdt =in.readLine();
            F_m=Float.parseFloat(forceNdt);
            forceNdt =in.readLine();
            dt=Float.parseFloat(forceNdt);
            FmDt=F_m*dt*dt;
            String line;
            int ind=0;
            while((line=in.readLine())!=null&&!line.equals(" ")){
                String[] cuts =line.split(" ");
                float x_t = Float.parseFloat(cuts[0]);
                float y_t = Float.parseFloat(cuts[1]);
                float r = Float.parseFloat(cuts[2]);
                noise_arr.add(ind++, new Pair<>(new Point2D.Float(x_t, y_t), r));
            }

            f=new Finder();
            path = f.findPath(100,noise_arr);
            true_path();

        }
        catch(Exception e){
            throw e;
        }
    }

    public void initRobots(int n){
        for (int i = 0; i <n ; i++) {
            robots.add(i,new Robot(noise_arr,true_path,F_m,dt));
        }
    }

    public Robot newBot(Robot bot){
        int i=robots.indexOf(bot);
        robots.remove(i);
        robots.add(i,new Robot(noise_arr,true_path,F_m,dt));
        robots.get(i).setChromoLength(chromoLength);
        return  robots.get(i);

    }

    public void setEtalonChromoLength(int sample){
        chromoLength=sample;
        for (Robot r: robots){
            r.setChromoLength(chromoLength);
        }
    }


    public float getParam(){
        return  FmDt;
    }


    public void initElite(int n) {
        for (int i = 0; i <n ; i++) {
            elite.add(i,new Robot(noise_arr,true_path,F_m,dt));
        }
    }
}
