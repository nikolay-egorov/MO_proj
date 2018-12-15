import A_Star.Finder;
import A_Star.Node;
import com.sun.javafx.geom.Vec2f;
import javafx.util.Pair;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class test_paths {
    public static void main(String[] args) {



        int ind = 0;
        ArrayList<Pair<Point2D, Float>> noise_arr = new ArrayList<>();
        noise_arr.add(ind++, new Pair<>(new Point2D.Float(0.5f, 0.5f), 0.3f));
        noise_arr.add(ind++, new Pair<>(new Point2D.Float(0.22f, 0.3f), 0.1f));
        noise_arr.add(ind++, new Pair<>(new Point2D.Float(0.02f, 0.11f), 0.01f));

        float dt = 0.25f;
        float F_m = 3;

        Point2D target = new Point2D.Float(1, 1);
        Point2D pos = new Point2D.Float(0, 0);
        System.out.println(Math.atan2(0, 0));
        System.out.println(Math.toRadians(Math.atan2(1, 1)));
//        float angl = (float) Math.toDegrees(Math.atan2(((pos.getX() - target.getX()) * (pos.getX() - target.getX())), ((pos.getY() - target.getY()) * (pos.getY() - target.getY()))));
//
//        System.out.println(angl);
        Finder f=new Finder();
        ArrayList<Node> path= f.findPath(100,noise_arr);
//        for (Node i :path){
//            //System.out.println(i.getPosition());
//             System.out.println("node : (" + i.getX()/100f + "," + i.getY()/100f + ")");
//
//        }

        Stack<Point2D> tr_path=true_path(path);
        boolean U_final=false,foundF_k=false;
        Vec2f f_k=new Vec2f(0,0);
        Vec2f U=new Vec2f(0,0);
        float radious=F_m*dt*dt;
        float U_x_final=0,U_y_final=0;
        int counter=0;
//        while ((!pos.equals(target)&&!U_final)){
        while ((pos.getX()<=0.98f||pos.getY()<0.98f)&&!U_final){
            int k=checkIfPosib(pos,tr_path,U,radious,dt);
            System.out.println("K is: "+k);

            if(k!=-1){
                f_k.set(((float) (tr_path.get(tr_path.size()-k-1).getX() )),(float)(tr_path.get(tr_path.size()-k-1).getY() ));
                for (int i = 0; i < k-1; i++) {
                    tr_path.pop();
                }

                System.out.println("New acclereation is: "+ f_k);

            }
            else{
                radious-=0.07;
                continue;
            }
//            U=setU(f_k,U);

            U.set((float)(f_k.x - pos.getX()),(float)(f_k.y - pos.getY()));
            System.out.println("Speed is: "+U);
            setPos(U,pos,dt);
            if(checkCollision(noise_arr,pos)){
                System.out.println("DEAD!");
               // return;
            }

            System.out.println("Pos is: "+ pos);
            counter++;
            if(pos.getX()>0.98f||pos.getY()>0.98f){
             //    U_x_final=(float)(1f - pos.getX())/dt;
                 U_x_final=(float)(1f - pos.getX())/dt;

             //    U_y_final=(float)(1f - pos.getY())/dt;
                 U_y_final=(float)(1f - pos.getY())/dt;
                if(U_x_final*U_x_final+U_y_final*U_y_final<=F_m*F_m)
                    U_final=true;
                else{
                    radious-=0.07;
                }
            }

        }

        U.set(U_x_final,U_y_final);
        System.out.println("Speed is: "+ U);
        setPos(U,pos,dt);
        System.out.println("\n\nFinal!\nPos is: "+ pos);
        counter++;
        U.set((float)( pos.getX()-f_k.x ),(float)( pos.getY()- f_k.y  ));
        System.out.println("Speed is: "+U);
        System.out.println("Steps: "+counter);

    }

    public static Vec2f setU(Vec2f curFdt, Vec2f  U ){


        U.x+=curFdt.x ;
        U.y+=curFdt.y;
        if(U.x<0 &&U.y<0){
            U.x=U.y=0;
        }
        return U;
    }

    public static void setPos(Vec2f U,Point2D C,float dt){
        float x= (float) (C.getX()+ dt*U.x);
        float y= (float) (C.getY()+ dt*U.y);
        C.setLocation(x,y);
    }




    public static int checkIfPosib(Point2D current, Stack<Point2D> tr_path,Vec2f U,float r,float dt){
        int k=-1;
        float x= (float) (current.getX()+U.x*dt);
        float y= (float) (current.getY()+U.y*dt);
     //   float r_n=F_m*dt*dt;
        for (int i=0;i<tr_path.size();i++){
            Point2D p=tr_path.get(i);
            if ((p.getX() - x) * (p.getX() - x) + (p.getY() - y) * (p.getY() - y) <= r * r){
                k++;

            }
        }
        return k;
    }

    public static Stack<Point2D> true_path(ArrayList<Node>path){

        path.add( new Node(99,99,"NORMAL"));
        path.add( new Node(100,100,"NORMAL"));
        Stack<Point2D> tr_path=new Stack<>();
        Collections.reverse(path);
        for (Node i:path){
            Point2D p=new Point2D.Float(i.getY()/100f,i.getX()/100f);
            tr_path.add(p);
        }
        return tr_path;
    }

    public static float getAngle(Point2D pos, Point2D target){
        float angle=  (float) Math.toDegrees(Math.atan2(((pos.getX()-target.getX())*(pos.getX()-target.getX())), ((pos.getY()-target.getY())*(pos.getY()-target.getY()))));
        if(angle < 0){
            angle += 360;
        }
        return (float) Math.toRadians(angle);
        //return  (float) Math.toRadians(Math.atan2(((pos.getX()-target.getX())*(pos.getX()-target.getX())), ((pos.getY()-target.getY())*(pos.getY()-target.getY()))));
    }
    public static boolean checkCollision(ArrayList<Pair<Point2D,Float>> noise,Point2D pos){
        for (int i = 0; i < noise.size(); i++){
            double R=noise.get(i).getValue();
            double dx=Math.abs(pos.getX()-noise.get(i).getKey().getX());
            if(dx>R)
                return false;
            double dy=Math.abs(pos.getY()-noise.get(i).getKey().getY());
            if(dy>R)
                return false;
            if(dx+dy <=R)
                return true;
            return  (dx*dx +dy*dy<=R*R);

        }
        return false;
    }

}
