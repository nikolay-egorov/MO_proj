package BinCode;

import com.sun.javafx.geom.Vec2d;
import com.sun.javafx.geom.Vec2f;
import javafx.util.Pair;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.*;
import static java.lang.Math.sin;

public class BinCode {


    public  Pair<String,String> getCodedRandF(float Fm){
        Point2D.Float p=getRandMove(Fm);
        System.out.println(p);
        return new Pair<>(convertFloatToBin(p.x), convertFloatToBin(p.y));
    }

    public  Point2D.Float getDecodedF(Pair<String,String> coded){
        return new Point2D.Float( convertBinToFloat(coded.getKey()), convertBinToFloat(coded.getValue()));
    }

    public  static   double convertBinToDouble(String myBinStr)
    {
        double myDbl = 0.0;
        if (myBinStr.length() > 63)
        {
            int sign=1;
            if(myBinStr.charAt(0)=='1'){
                sign=-1;
            }
            String negBinStr = myBinStr.substring(1);
            myDbl = sign * Double.longBitsToDouble(Long.parseLong(negBinStr, 2));
        }
        else
        {
            myDbl = Double.longBitsToDouble(Long.parseLong(myBinStr, 2));
        }
        return myDbl;
    }

    public static String convertDoubleToBin(double doubleValue){
        String binaryString = Long.toBinaryString(Double.doubleToLongBits(doubleValue));
        StringBuilder builder = new StringBuilder();
        for (int i = binaryString.length(); i < 64; i++) {
            builder.append('0');
        }
        return  builder.append(binaryString).toString();
    }


    public static String convertVec2dToBin(Vec2d d){
        String binaryString = Long.toBinaryString(Double.doubleToLongBits(d.x));
        StringBuilder builder = new StringBuilder();
        for (int i = binaryString.length(); i < 64; i++) {
            builder.append('0');
        }
        builder.append(binaryString) ;
        binaryString = Long.toBinaryString(Double.doubleToLongBits(d.y));
        for (int i = binaryString.length(); i < 64; i++) {
            builder.append('0');
        }
        return  builder.append(binaryString).toString();
    }

    public static Vec2d convertBinToVec2d(String binary){
        Vec2d d=new Vec2d(0,0);

        String a=binary.substring(0,Double.SIZE);
        String b=binary.substring(Double.SIZE);
        d.set(convertBinToDouble(a),convertBinToDouble(b));
        return  d;
    }

    public  String convertFloatToBin(float f){
        int intBits = Float.floatToIntBits(f);
        String result = Integer.toBinaryString(intBits);
        StringBuilder builder = new StringBuilder();
        for (int i = result.length(); i <= 32; i++) {
            builder.append('0');
        }
        return builder.append(result).toString();

    }

    public static String convertVector2fToBin(Vec2f f){
        int intBits = Float.floatToIntBits(f.x);
        String result = Integer.toBinaryString(intBits);
        StringBuilder builder = new StringBuilder();
        for (int i = result.length(); i < 32; i++) {
            builder.append('0');
        }
        builder.append(result);
        intBits=Float.floatToIntBits(f.y);
        result = Integer.toBinaryString(intBits);
        for (int i = result.length(); i < 32; i++) {
            builder.append('0');
        }
        return builder.append(result).toString();
    }

    public static Vec2f convertBinToVec2f(String binary){
        Vec2f f=new Vec2f(0,0);

        String a=binary.substring(0,Float.SIZE);
        String b=binary.substring(Float.SIZE);
        f.set(convertBinToFloat(a),convertBinToFloat(b));
        return f;
    }


    public  static float convertBinToFloat(String binary){
        if(binary.length()<32){
            return  Float.intBitsToFloat(Integer.parseInt(binary,2));
        }
        else {
            int sign=1;
            if(binary.charAt(0)=='1'){
                sign=-1;
            }
            return sign*Float.intBitsToFloat(Integer.parseInt(binary.substring(1),2));
        }
    }
    /**
     * fix to rand needed
     * @param Fm
     * @return
     */
    public static  Point2D.Float getRandMove(float Fm){
        Random generator = new Random();
        generator.setSeed(121);
        double r=  (Fm*sqrt(random()));
        double theta= (random()*2*PI);
        double x=r*cos(theta);
        double y=r*sin(theta);
        return new Point2D.Float( Math.round(x * 100000f) / 100000f, Math.round(y * 100000f) / 100000f);

    }

}
