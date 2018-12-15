import A_Star.*;
import BinCode.BinCode;
import com.sun.javafx.geom.Vec2f;
import javafx.util.Pair;

import java.awt.geom.Point2D;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;
import java.util.Vector;

public class Robot {
    private ArrayList<Vec2f> speeds;
    private ArrayList<Vec2f> forces;
    private ArrayList<Node> path;
    private Point2D location;
    private float F_m;
    private float dt;
    protected int moves;
    private  float maxRad;
    private  float rad;
    private int ind;
    ArrayList<Pair<Point2D, Float>> noise_arr;
    protected int[] genes;
    protected int offset;
    protected  float penalty;
    Stack<Point2D> true_path;
    protected int chromoLength;
    protected  Stack<Integer> genom;
    public static final ActionType DEFAULT_ACTION_TYPE = ActionType.FOLLOW;
    public enum ActionType{
        RUN, FOLLOW;
    }


    public Robot(ArrayList<Pair<Point2D, Float>> noise,Stack<Point2D> tr_path,float Fm,float dt){
        speeds=new ArrayList<>();
        speeds.add(new Vec2f(0,0));
        forces=new ArrayList<>();
        noise_arr=noise;
        true_path= (Stack<Point2D>) tr_path.clone();
        location=new Point2D.Float(0,0);
        moves=0;
        offset=0;
        F_m=Fm;
        this.dt=dt;
        penalty=0;

    }

    public void setGene(int[] chromo){
        genes=new int[chromoLength];

        for (int i=chromoLength-chromo.length;i<chromoLength;i++){
            for (int j=0;j<chromo.length;j++)
                genes[i]=chromo[j];
        }

    }

    public void startShow(float Fmdt){
        rad = maxRad = Fmdt;
        ind = 0;
        float U_x_final = 0, U_y_final = 0;
        while ((location.getX() <= 0.98f || location.getY() <= 0.98f)) {
            int k = checkForPosibRoute(speeds.get(ind), rad);
            if (k != -1) {
                forces.add(new Vec2f(((float) (true_path.get(true_path.size() - k - 1).getX())), (float) (true_path.get(true_path.size() - k - 1).getY())));
                for (int i = 0; i < k - 1; i++) {
                    true_path.pop();
                }

                //    System.out.println("New acclereation is: "+ f_k);

            } else {
                rad -= 0.09;
                if(ind!=0) {
                    ind--;
                }
                continue;
            }


            speeds.add(ind,new Vec2f((float) (forces.get(ind).x - location.getX()), (float) (forces.get(ind).y - location.getY())));
            //      System.out.println("Speed is: "+U);
            setPos(ind);
            if (checkCollision(location)) {
                System.out.println("DEAD!");
                ind--;
                return;
            }

            //    System.out.println("Pos is: "+ pos);
            moves++;
            ind++;
            if (location.getX() > 0.98f || location.getY() > 0.98f) {

                U_x_final = (float) (1f - location.getX()) / dt;
                U_y_final = (float) (1f - location.getY()) / dt;
                if (U_x_final * U_x_final + U_y_final * U_y_final <= F_m * F_m){
                    int a=1;
                }
                // U_final=true;
//                            System.out.println("g");
                else {
                    rad -= 0.09;
                    ind--;
                    continue;
                }
            }
        }

        speeds.add(ind++, new Vec2f(U_x_final, U_y_final));
        setPos(ind);
        moves++;
        speeds.add(new Vec2f((float) (location.getX() - forces.get(ind-2).x), (float) (location.getY() - forces.get(ind-2).y)));
    }

    public Robot run(float Fmdt){

        while ((offset!=chromoLength&&offset+62< chromoLength)) {
            makeNextAction();
            ind++;
            if (checkCollision(location) || location.getY() < 0 || location.getY() < 0 || location.getX() > 1 || location.getY() > 1) {
                //   System.out.println("DEAD!");
                ind--;
                penalty += 500;
                return this;
            }

            if (location.getX() >= 0.999f || location.getY() >= 0.999f) {
                break;
            }
        }
    return  this;
    }

    public void makeNextAction(){
        StringBuilder builder=new StringBuilder();

        for(int i=offset;i<=offset+62;i++){
            builder.append(genes[i]);

        }
        offset+=62;
        Vec2f f=BinCode.convertBinToVec2f(builder.toString());
        if(f.y==0&&f.x==0){
            ind--;
            return;
        }
        forces.add(f);
        if(ind==0){
            speeds.add(ind,new Vec2f((float) (forces.get(ind).x - location.getX()), (float) (forces.get(ind).y - location.getY())));
        }
        else
            speeds.add(ind,new Vec2f((float) (forces.get(ind-1).x - location.getX()), (float) (forces.get(ind-1).y - location.getY())));
        setPos(ind);
        moves++;
    }

    public Point2D showLocation(){
        return location;
    }


    public  boolean checkCollision(Point2D p){
        for (int i = 0; i < noise_arr.size(); i++){
            double R=noise_arr.get(i).getValue();
            double dx=Math.abs(p.getX()-noise_arr.get(i).getKey().getX());
            if(dx>R)
                return false;
            double dy=Math.abs(p.getY()-noise_arr.get(i).getKey().getY());
            if(dy>R)
                return false;
            if(dx+dy <=R)
                return true;
            return  (dx*dx +dy*dy<=R*R);

        }
        return false;
    }

    public float dist(Point2D target){
        return (float) Math.abs(Math.sqrt((location.getX()-target.getX())*(location.getX()-target.getX()) +(location.getY()-target.getY())*(location.getY()-target.getY())));
    }

    //To-DO FITNESS!
    public float distOverMoves(Point2D target){
//        return dist(target)/moves;
        float penalty=0;
   //     return  ((float) 1/(0.7f*dist(target)) -  moves - penalty); //+ Math.log10(moves)*0.6f));
         return  ((float) 1/(0.1f*dist(new Point2D.Float(0,0))+0.3f*dist(target) -  0.5f*moves) - penalty ); //+ Math.log10(moves)*0.6f));
    }


    public  int checkForPosibRoute( Vec2f U,float r ){
        int k=-1;
        float x= (float) (location.getX()+U.x*dt);
        float y= (float) (location.getY()+U.y*dt);
        //   float r_n=F_m*dt*dt;
        for (int i=0;i<true_path.size();i++){
            Point2D p=true_path.get(i);
            if ((p.getX() - x) * (p.getX() - x) + (p.getY() - y) * (p.getY() - y) <= r * r){
                k++;

            }
        }
        return k;
    }

    public  void setPos(int index){
        float x= (float) (location.getX()+ dt*speeds.get(index).x);
        float y= (float) (location.getY()+ dt*speeds.get(index).y);
        location.setLocation(x,y);
    }

    public void setChromoLength(int length){
        chromoLength=length;
        genes=new int[chromoLength];
    }


    public int[] getEliteGene(){
        StringBuilder builer=new StringBuilder();
        for (Vec2f f: forces){
            builer.append(BinCode.convertVector2fToBin(f));
        }
        String result=builer.toString();
        genes=new int[result.length()];
        for (int i=0;i<result.length();i++){
            genes[i]=Character.getNumericValue(result.charAt(i));
        }
        return genes;
    }

    public int[] getGenes(){
        StringBuilder builer=new StringBuilder();
        for (Vec2f f: forces){
            builer.append(BinCode.convertVector2fToBin(f));
        }
        String result=builer.toString();
            for(int i=result.length();i<chromoLength;i++){
                builer.append(0);
            }
            result=builer.toString();



        for (int i=0;i<result.length();i++){
            genes[i]=Character.getNumericValue(result.charAt(i));
        }
        return genes;
    }





}
