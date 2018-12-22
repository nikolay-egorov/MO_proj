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
    private  float rad;
    private int ind;
    protected boolean ifElite;
    ArrayList<Pair<Point2D, Float>> noise_arr;
    protected int[] genes;
    protected int offset;
    protected  float penalty;
    protected  int[] penalty_ind;
    Stack<Point2D> true_path;
    protected int chromoLength;
    protected  Stack<Integer> genom;
    public static final ActionType DEFAULT_ACTION_TYPE = ActionType.FOLLOW;
    public enum ActionType{
        RUN, FOLLOW;
    }


    public Robot(ArrayList<Pair<Point2D, Float>> noise,Stack<Point2D> tr_path,float Fm,float dt,boolean ifElite){
        speeds=new ArrayList<>();
        speeds.add(new Vec2f(0,0));
        forces=new ArrayList<>();
        noise_arr=noise;
        true_path= (Stack<Point2D>) tr_path.clone();
        location=new Point2D.Float(0,0);
        this.ifElite=ifElite;
        if(ifElite)
            penalty_ind=new int[2001];

        moves=0;
        offset=0;
        F_m=Fm;
        this.dt=dt;
        penalty=0;
     }

    public int[] setGene(int[] chromo){
        genes=new int[chromoLength];

        for (int i=chromoLength-chromo.length;i<chromoLength;i++){
            for (int j=0;j<chromo.length;j++)
                genes[i]=chromo[j];
        }
        return  genes;
    }

    public void startShow(float Fmdt){
        rad = Fmdt;
        ind = 0;
        int clearRun=0;
        float U_x_final = 0, U_y_final = 0;
        boolean U_final=false,F_final=false;

        while (!(U_final && F_final)) {
            int k = checkForPosibRoute(speeds.get(ind), rad);
            if (U_final)
                F_final=true;
            if(k==true_path.size()-1){
                U_final=true;
            }
            if (k>=0) {
                 if(ind!=0&& ind<=forces.size()-1 ) {
                    forces.set(ind,new Vec2f((float) ((1 / dt) * (((true_path.get(k).getX() - location.getX()) / dt) - speeds.get(ind).x)),
                            (float) ((1 / dt) * (((true_path.get(k).getY() - location.getY()) / dt) - speeds.get(ind).y))));
                }
                else
                    forces.add(new Vec2f((float) ((1 / dt) * (((true_path.get(k).getX() - location.getX()) / dt) - speeds.get(ind).x)),
                            (float) ((1 / dt) * (((true_path.get(k).getY() - location.getY()) / dt) - speeds.get(ind).y))));

                //    System.out.println("New acclereation is: "+ f_k);
                clearRun++;
            } else {
                clearRun=0;
                U_final=false;
                F_final=false;
                //был штраф, те были ли мы здесь?
                if(penalty_ind[ind]!=0) {
                    if (penalty_ind[ind ] != Fmdt) {
                        penalty_ind[ind ] -= 3;
                        continue;
                    }
                }
                else {
                    if (ind!=0)
                        penalty_ind[ind - 1] -=5;
                    undoPos(ind );
                    ind--;
                }

                continue;
            }

            if (speeds.size()!=0&&ind+1<speeds.size()&&ind!=0)
                speeds.set(ind+1,new Vec2f( (forces.get(ind).x*dt + speeds.get(ind ).x),   (forces.get(ind).y*dt + speeds.get(ind ).y)));
            else {
                speeds.add( new Vec2f((forces.get(ind).x * dt + speeds.get(ind  ).x), (forces.get(ind).y * dt + speeds.get(ind  ).y)));
            }


            //      System.out.println("Speed is: "+U);
            setPos(ind);
            if (checkCollision(location)) {
                System.out.println("DEAD!");
                clearRun=0;
                U_final=false;
                F_final=false;
                penalty_ind[ind-1]  +=-1;
                 if(ind!=0&&penalty_ind[ind]==0) {
                    undoPos(ind );
                    ind--;
                }
                continue;
            }

            //    System.out.println("Pos is: "+ pos);
            moves++;
            ind++;

        }

 //        moves++;
    }

    public Robot run(float Fmdt){

        while ((location.getX() != 1 || location.getY() != 1) &&( offset+64<chromoLength||offset!=chromoLength) ) {
            makeNextAction();
            ind++;
            if (checkCollision(location) || location.getY() < 0 || location.getY() < 0 || location.getX() > 1 || location.getY() > 1) {
                //   System.out.println("DEAD!");
                penalty += 100;
                return this;
            }

        }
        return  this;
    }

    public void makeNextAction(){
        StringBuilder builder=new StringBuilder();
        Vec2f U=new Vec2f(speeds.get(ind));
        if(offset==chromoLength-64){
            for(int i=offset;i<chromoLength;i++){
                builder.append(genes[i]);
            }
        }
        else {
            for (int i = offset; i <  offset + 64; i++) {
                builder.append(genes[i]);
            }
        }
        offset+=64;
        Vec2f f=BinCode.convertBinToVec2f(builder.toString());
//        if(f.y==0&&f.x==0){
//            ind--;
//            return;
//        }
        forces.add(ind,f);
        speeds.add(new Vec2f( (forces.get(ind).x*dt + U.x), (forces.get(ind).y*dt  + U.y)));

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

    //TODO FITNESS!
    public float distOverMoves(Point2D target){
//        return moves-penalty-dist(new Point2D.Float(0,0))*100;

//        return  ((float) (  0.3f*moves - 0.3f*penalty)/(0.3f*dist(target)+0.1f*dist(new Point2D.Float(0,0))  )  ); //+ Math.log10(moves)*0.6f));
        return  ((float) 1/(0.3f*dist(target)+0.3f*dist(new Point2D.Float(0,0)))-0.3f*moves - 0.3f*penalty); //+ Math.log10(moves)*0.6f));
//         return  ((float) 1/(0.1f*dist(new Point2D.Float(0,0))+0.3f*dist(target) -  0.5f*moves) - penalty ); //+ Math.log10(moves)*0.6f));
    }


    public  int checkForPosibRoute( Vec2f U,float r ){
        int k=-1;
         if(location.getX()==1&&location.getY()==1){
            if (( - U.x*dt) * ( - U.x*dt) + ( - U.y*dt) * ( - U.y*dt) <= r * r){
                return true_path.size()-1;
            }
            else return k;
        }

        //   float r_n=F_m*dt*dt;
        for (int i=0;i<true_path.size();i++){
            Point2D p=true_path.get(i);
            if ((p.getX() - location.getX() - U.x*dt) * (p.getX() - location.getX() - U.x*dt) + (p.getY() - location.getY() - U.y*dt) * (p.getY() - location.getY() - U.y*dt) <= r * r){
                k=i;
            }
        }
        return k + penalty_ind[ind];
    }

    public  void setPos(int index){
        float x= (float) (location.getX()+ dt*speeds.get(index+1).x);
        float y= (float) (location.getY()+ dt*speeds.get(index+1).y);
        location.setLocation(x,y);
    }

    public  void undoPos(int index){
        float x= (float) (location.getX()- dt*speeds.get(index).x);
        float y= (float) (location.getY()- dt*speeds.get(index).y);
        location.setLocation(x,y);
    }

    public void setChromoLength(int length){
        chromoLength=length;
        genes=new int[chromoLength];
    }


    public int[] getEliteGene(){
        String gene;
        int k=0,i;
        int length=64*forces.size();
        genes=new int[length];
        for (Vec2f f: forces){
            gene=BinCode.convertVector2fToBin(f);
            for (int j=0;j<gene.length();j++,k++)
                genes[k]=Character.getNumericValue(gene.charAt(j));
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
