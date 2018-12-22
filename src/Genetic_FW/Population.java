package Genetic_FW;

import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Random;
import java.util.Arrays;
import java.util.Comparator;

public class Population {

    private Individ population[];
    private double populationFitness = -1;
    protected int chromoLength;
    protected  Point2D location;
    /**
     * Initializes blank population of Individs
     */
    public Population(int populationSize) {
        // Initial population
        this.population = new Individ[populationSize];
    }

    /**
     * Initializes population of individuals
     */
    public Population(int populationSize, int chromosomeLength) {
        // Initialize the population as an array of individuals
        this.population = new Individ[populationSize];

        // Create each individual in turn
        for (int individualCount = 0; individualCount < populationSize; individualCount++) {
            // Create an individual, initializing its chromosome to the given
            // length
            Individ individual = new Individ(chromosomeLength,false);
            // Add individual to population
            this.population[individualCount] = individual;
        }
    }

    public void setChromoLength(int length){
        chromoLength=length;
    }

    public Point2D getBestLocation(){
        return location;
    }

    public void setLocation(Point2D loc){
        location=loc;
    }

    public Population unifyIndivids(){
        Population population1=new Population(size());
        int k=0;
        for (Individ i:getIndivids()){
            Individ newInd=new Individ(chromoLength,false);
            for (int j=0;j<i.getChromosomeLength();j++)
                newInd.setGene(j,i.getGene(j));
            for (int j=i.getChromosomeLength();j<chromoLength;j++) {
                newInd.setGene(j, 0);
            }
            population1.setIndivid(k++,i);
        }
        return population1;
    }
    /**
     * Get individuals from the population
     */
    public Individ[] getIndivids() {
        return this.population;
    }

    /**
     * Find an individual in the population by its fitness
     *
     * This method lets you select an individual in order of its fitness. This
     * can be used to find the single strongest individual (eg, if you're
     * testing for a solution), but it can also be used to find weak individuals
     * (if you're looking to cull the population) or some of the strongest
     * individuals (if you're using "elitism").
     *
     * @param offset
     *            The offset of the individual you want, sorted by fitness. 0 is
     *            the strongest, population.length - 1 is the weakest.
     * @return individual Individual at offset
     */
    public Individ getFittest(int offset) {
        // Order population by fitness
        Arrays.sort(this.population, new Comparator<Individ>() {
            @Override
            public int compare(Individ o1, Individ o2) {
                if (o1.getFitness() > o2.getFitness()) {
                    return -1;
                } else if (o1.getFitness() < o2.getFitness()) {
                    return  1;
                }
                return 0;
            }
        });

        // Return the fittest Individ
        return this.population[offset];
    }


    /**
     * Set population's group fitness
     */
    public void setPopulationFitness(double fitness) {
        this.populationFitness = fitness;
    }

    /**
     * Get population's group fitness
     */
    public double getPopulationFitness() {
        return this.populationFitness;
    }

    /**
     * Get population's size
     */
    public int size() {
        return this.population.length;
    }

    /**
     * Set individ at offset
     */
    public Individ setIndivid(int offset, Individ individ) {
        return population[offset] = individ;
    }

    /**
     * Get individ at offset
     */
    public Individ getIndivid(int offset) {
        return population[offset];
    }

    /**
     * Shuffles the population in-place
     * @return void
     */
    public void shuffle() {
        Random rnd = new Random();
        for (int i = population.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            Individ a = population[index];
            population[index] = population[i];
            population[i] = a;
        }
    }
}
