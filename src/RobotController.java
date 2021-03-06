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
    protected float F_m;
    protected float dt;
    private int chromoLength;
    protected boolean noWay;
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
        noWay=false;
    }


    public void true_path( ){
        path.add( new Node(999,999,"NORMAL"));
        path.add( new Node(1000,1000,"NORMAL"));
        true_path =new Stack<>();
//        Collections.reverse(path);
        for (Node i:path){
            Point2D p=new Point2D.Float(i.getY()/1000f,i.getX()/1000f);
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
            path = f.findPath(1000,noise_arr);
            if (path==null)
                noWay=true;
            if (noWay)
                throw new Exception("No way found");
            true_path();

        }
        catch(Exception e){
            throw e;
        }
    }

    public void initRobots(int n){
        for (int i = 0; i <n ; i++) {
            robots.add(i,new Robot(noise_arr,true_path,F_m,dt,false));
        }
    }

    public Robot newBot(Robot bot){
        int i=robots.indexOf(bot);
        robots.remove(i);
        robots.add(i,new Robot(noise_arr,true_path,F_m,dt,false));
        robots.get(i).setChromoLength(chromoLength);
        return  robots.get(i);

    }

    public void setEtalonChromoLength(int sample){
        chromoLength=sample;
        for (Robot r: robots){
            r.setChromoLength(chromoLength);
        }

//        for (int i=1;i<elite.size();i++)
//            elite.get(i).setChromoLength(chromoLength);
    }


    public int geEtalonChromoLength(){
        return  chromoLength;
    }


    public void initElite(int n) {
        for (int i = 0; i <n ; i++) {
            elite.add(i,new Robot(noise_arr,true_path,F_m,dt,true));
        }
    }
}
