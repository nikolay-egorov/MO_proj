import javafx.util.Pair;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.*;

import  BinCode.BinCode.*;


public class Main {

    public static void main(String[] args) throws Exception {
        File file =
                new File("input.txt");
        Scanner in = new Scanner(file);
        float F_x, F_y, x = 0, y = 0, U_x = 0, U_y = 0;
        int F_max, individ_n = 10, ind = 0;
        float dt;
        ArrayList<Pair<Point2D.Float, Float>> noise_arr = new ArrayList<Pair<Point2D.Float, Float>>();

        //get data from file
        F_max = in.nextInt();
        dt = in.nextFloat();

        while (in.hasNext()) {
            float x_t = in.nextFloat();
            float y_t = in.nextFloat();
            float r = in.nextFloat();
            noise_arr.add(ind++, new Pair<>(new Point2D.Float(x_t, y_t), r));

        }

        for (int i = 0; i < noise_arr.size(); i++) {
            System.out.println(noise_arr.get(i).getKey() + " " + noise_arr.get(i).getValue());
        }

/*
        System.out.println(F_max);
*/
/*
        Pair<String,String> coded = getCodedRandF(F_max);
        System.out.println(coded.getKey() +" " +coded.getValue());
        //best go for Point2D,not Point2d.float
        Point2D.Float decoded=getDecodedF(coded);
        ArrayList<Pair<String,String>> genom=new ArrayList<>();
        noise_arr.sort((Comparator<? super Pair<Point2D.Float, Float>>) createSpecialComparator(new Point2D.Float(0,0)));
        for (int i = 0; i < noise_arr.size(); i++) {
            System.out.println(noise_arr.get(i).getKey() + " " + noise_arr.get(i).getValue());
        }

*//*



        int iters=0;
        float x_k = x;
        float y_k = y;
        float U_xk=U_x;
        float U_yk=U_y;

        while (true) {

            if (Math.abs(x) < 1 && Math.abs(y) < 1&&!checkCollision(noise_arr,new Point2D.Float(x_k,y_k))) {
                U_xk=U_x + dt * decoded.x;
                U_yk = U_y + dt * decoded.y;
                x_k = x + dt * U_xk;
                y_k = y + dt * U_yk;
                System.out.println("x y position: " + x + ":" + y+" Speed is:"+ Math.sqrt(U_xk*U_xk+U_yk*U_yk));

                iters++;
                if (x_k == 1 && y_k == 1) {
                    System.out.println("REACHED: " + x + ":" + y);
                    return;
                }



                U_x = U_xk;
                U_y = U_yk;
                x = x_k;
                y = y_k;
                genom.add(coded);
                coded=getCodedRandF(F_max);
                decoded=getDecodedF(coded);

            } else {
                System.out.println("Simulation failed: "+iters +" iterations");
                break;
            }
        }

//        for(Pair<String,String> p : genom){
//            System.out.println(p.getKey()+" "+p.getValue());
//        }
*/

    }




    public static boolean checkCollision(ArrayList<Pair<Point2D.Float,Float>> noise,Point2D p){
        for (int i = 0; i < noise.size(); i++){
            double R=noise.get(i).getValue();
            double dx=Math.abs(p.getX()-noise.get(i).getKey().getX());
            if(dx>R)
                return false;
            double dy=Math.abs(p.getY()-noise.get(i).getKey().getY());
            if(dy>R)
                return false;
            if(dx+dy <=R)
                return true;
            return  (dx*dx +dy*dy<=R*R);

        }
        return false;
    }
/*
    public static boolean inCircle( int x, int y ){
        int dx = ABS(x-xo);
        if (    dx >  R ) return false;
        int dy = ABS(y-yo);
        if (    dy >  R ) return false;
        if ( dx+dy <= R ) return true;
        return ( dx*dx + dy*dy <= R*R );
    }*/
    private static Comparator<? super Pair<Point2D, Float>> createSpecialComparator(Point2D.Float p)
    {
        final Point2D finalP = new Point2D.Float((float)p.getX(),(float) p.getY());
        return new Comparator<Pair<Point2D, Float>>() {
            @Override
            public int compare(Pair<Point2D, Float> p0, Pair<Point2D, Float> p1) {
                float ds0 = (float)p0.getKey().distanceSq(finalP);
                float ds1 =(float) p1.getKey().distanceSq(finalP);
                return Float.compare(Math.abs(p0.getValue()- ds0), Math.abs(p1.getValue()- ds1));
            }
        };
    }



}