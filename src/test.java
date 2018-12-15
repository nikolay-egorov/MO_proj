import Genetic_FW.Individ;
import Genetic_FW.Population;
import com.sun.javafx.geom.Vec2f;

import java.awt.geom.Point2D;
import  BinCode.BinCode;
public class test {

    public static void main(String[] args) throws Exception {
//        float a=-0.26811f;
//        System.out.println(a);
//
//        String test=convertFloatToBin(a);
//        System.out.println(test+ " size is "+test.length());
//
//        Pair<String,String> coded = BinCode.getCodedRandF(a);
//        System.out.println(coded.getKey() +" " +coded.getValue());
//
//        Point2D.Float p=getDecodedF(coded);
//        System.out.println("Decoded/n"+p.x+" "+p.y);
//

/*
        Vec2f f=new Vec2f(1.2f,0.312f);
        String a=BinCode.convertVector2fToBin(f);

        System.out.println(f+"string size "+a.length());
        System.out.println(BinCode.convertBinToVec2f(a));
*/


        RobotController controll=new RobotController();
        controll.initFromF("input.txt");
        Genetic_FrWork ga = new Genetic_FrWork(33, 15, 31, 1, 4);
        Population population=ga.initPopulationBySample(controll.FmDt,9);
        population=ga.runFirstGen(33,controll);
         ga.evalPopulation(population,controll);
        System.out.println((float) population.getFittest(0).getFitness());
        int generation = 1;
        // Start evolution loop

        while (ga.isTerminationConditionMet(generation,10000) == false) {
            // Print fittest individual from population
           // Individ fittest = population.getFittest(population.size()-1);
            Individ fittest = population.getFittest(0);
            System.out.println(
                    "G" + generation + " Best solution (" + fittest.getFitness() + ") "+ga.getFittestRobotLocation(population,controll));
            // Apply crossover
//                population = ga.newCrossover(population);
             population = ga.crossoverPopulation(population);

            // Apply mutation
            population = ga.mutatePopulation(population);

            // Evaluate population
            ga.evalPopulation(population,controll);
            // Increment the current generation
            generation++;
           // System.out.println(controll.robots.get(0).showLocation());

        }

        System.out.println("Stopped after " + generation + " generations.");
        Individ fittest = population.getFittest(0);
        System.out.println("Best solution (" + fittest.getFitness() + "): ");  //+ fittest.toString());



    }
    public static float getAngle(Point2D pos, Point2D target){
         float angle=  (float) Math.toDegrees(Math.atan2(((pos.getX()-target.getX())*(pos.getX()-target.getX())), ((pos.getY()-target.getY())*(pos.getY()-target.getY()))));
        if(angle < 0){
            angle += 360;
        }
        return (float) Math.toRadians(angle);
         //return  (float) Math.toRadians(Math.atan2(((pos.getX()-target.getX())*(pos.getX()-target.getX())), ((pos.getY()-target.getY())*(pos.getY()-target.getY()))));
    }



}
