import Genetic_FW.Individ;
import Genetic_FW.Population;
import com.sun.javafx.geom.Vec2d;
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
        Vec2d f=new Vec2d(1.2d,0.312d);
        String a=BinCode.convertVec2dToBin(f);

        System.out.println(f+"string size "+a.length());
        System.out.println(BinCode.convertBinToVec2d(a));
*/




         RobotController controll=new RobotController();
//        controll.initFromF("input.txt");
        controll.initFromF("tests for GA/test1.txt");
        Genetic_FrWork ga = new Genetic_FrWork(13, 0.05, 1, 2, 4);
        ga.initPopulationBySample(controll.FmDt,1);
        Population population=ga.runFirstGen(13,controll);
   //     population=population.unifyIndivids();
        ga.evalPopulation(population,controll);

/*
        for (Individ fittest:population.getIndivids())
            System.out.println(fittest.getFitness());
*/


        System.out.println((float) population.getFittest(0).getFitness());
        int generation = 1;
//        System.out.println(
//                "G" + generation + " Best solution (" +  ") "+ga.getFittestRobotLocation(population,controll));
        // Start evolution loop
        population.setLocation(ga.getFittestRobotLocation(population,controll));

//        while (ga.isTerminationConditionMet(generation,10000) == false) {
        while (ga.isTerminationConditionMet(generation,10000) == false) {
            // Print fittest individual from population
           // Individ fittest = population.getFittest(population.size()-1);
            Individ fittest = population.getFittest(0);
            population.setLocation(ga.getFittestRobotLocation(population,controll));
            System.out.println(
                    "G" + generation + " Best solution (" + fittest.getFitness() + ") "+population.getBestLocation());
            // Apply crossover
      //       population = ga.newCrossover(population);
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
