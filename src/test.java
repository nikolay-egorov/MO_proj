import Genetic_FW.Population;

import java.awt.geom.Point2D;
import java.util.Scanner;

public class test {

    public static void main(String[] args) throws Exception {


        RobotController controll = new RobotController();

        System.out.println("Please do specify input data\nLocated in tests for GA package,enter only name: ");
        Scanner scanner = new Scanner(System.in);
        String response = scanner.nextLine();


        controll.initFromF("tests for GA/" + response);
        //    controll.initFromF("tests for GA/test1.txt");
        Genetic_FrWork ga = new Genetic_FrWork(13, 0.05, 1, 2, 4);

        ga.initPopulationBySample(controll.FmDt, 1);
        Population population = ga.runFirstGen(13, controll);
        WDrawer drawer = new WDrawer(controll);


        ga.evalPopulation(population, controll);
        drawer.draw();


        System.out.println((float) population.getFittest(0).getFitness());
        int generation = 1;

        /*
        Remove comments bellow if y want to see crackly GA
         */
////        System.out.println(
////                "G" + generation + " Best solution (" +  ") "+ga.getFittestRobotLocation(population,controll));
//        // Start evolution loop
//        population.setLocation(ga.getFittestRobotLocation(population,controll));


////        while (ga.isTerminationConditionMet(generation,10000) == false) {
//        while (ga.isTerminationConditionMet(generation,1000) == false) {
//            // Print fittest individual from population
//           // Individ fittest = population.getFittest(population.size()-1);
//            Individ fittest = population.getFittest(0);
//            population.setLocation(ga.getFittestRobotLocation(population,controll));
//            System.out.println(
//                    "G" + generation + " Best solution (" + fittest.getFitness() + ") "+population.getBestLocation());
//            // Apply crossover
//      //       population = ga.newCrossover(population);
//               population = ga.crossoverPopulation(population);
//
//            // Apply mutation
//            population = ga.mutatePopulation(population);
//
//            // Evaluate population
//            ga.evalPopulation(population,controll);
//            // Increment the current generation
//            generation++;
//           // System.out.println(controll.robots.get(0).showLocation());
//
//        }
//
//        System.out.println("Stopped after " + generation + " generations.");
//        Individ fittest = population.getFittest(0);
//        System.out.println("Best solution (" + fittest.getFitness() + "): ");  //+ fittest.toString());
//

    }


}
