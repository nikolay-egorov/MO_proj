import Genetic_FW.Individ;
import Genetic_FW.Population;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Genetic_FrWork {
    private int populationSize;
    private double mutationRate;
    private double crossoverRate;
    private int elitismCount;
    public Population population;
    List<Float> pattern;
    public enum CrossingType {
        ONE_POINT_RECOMBINATION, TWO_POINT_RECOMBINATION, ELEMENTWISE_RECOMBINATION, ONE_ELEMENT_EXCHANGE
    }

    public static final int OCTET_LENGTH = 62; // for 2f
    public static final int MASK_FOR_MOD = OCTET_LENGTH - 1;
    public static final int SHIFT_FOR_DIVISION;
    static {
        int shiftForDivision = 0;
        int tmp = OCTET_LENGTH;
        while (tmp > 1) {
            tmp >>= 1;
            shiftForDivision++;
        }
        SHIFT_FOR_DIVISION = shiftForDivision;
    }
    private Random random = new Random(System.currentTimeMillis());

    /**
     * A new property we've introduced is the size of the population used for
     * tournament selection in crossover.
     */
    protected int tournamentSize;

    public Genetic_FrWork(int populationSize, double mutationRate, double crossoverRate, int elitismCount,
                          int tournamentSize) {

        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.elitismCount = elitismCount;
        this.tournamentSize = tournamentSize;
    }

    /**
     * Initialize population
     *
     * @param chromosomeLength
     *            The length of the individuals chromosome
     * @return population The initial population generated
     */
    public Population initPopulation(int chromosomeLength) {
        // Initialize population
        population = new Population(this.populationSize, chromosomeLength);
        return population;
    }

    public Population initPopulationBySample(float Sample,int n){
        pattern=new ArrayList<>();
        for (int i = n; i >=1 ; i--) {
            pattern.add(Sample/i);
        }
        population=new Population(this.populationSize);

        return  population;
    }

    public Population runFirstGen(int n,RobotController controller){
        controller.initRobots(n);
        controller.initElite(pattern.size());
        int i ,chromoLength;
        for (i = 0; i <pattern.size() ; i++) {
            controller.elite.get(i).startShow(pattern.get(i));
            System.out.println(i+ " Done!");
            if(i==0){
                chromoLength=controller.elite.get(i).getEliteGene().length;
                controller.setEtalonChromoLength(chromoLength);

            }
            int[] gene=controller.elite.get(i).getEliteGene();
            population.setIndivid(i,new Individ(gene));
        }

        for (i=0;i<populationSize;i++){
                population.setIndivid(i,new Individ(population.getIndivid(this.random.nextInt(pattern.size())).getChromosome()));
        }
        System.out.println("First gen is out!!");
        return  population;
    }


    /**
     * Calculate fitness for an individual.
     *
     * This fitness calculation is a little more involved than chapter2's. In
     * this case we initialize a new Robot class, and evaluate its performance
     * in the given maze.
     *
     * @param individual
     *            the individual to evaluate
     * @param bot
     *            simulation simple
     * @return double The fitness value for individual
     */
    public double calcFitness(Individ individual, Robot bot,float param) {
        // Get individual's chromosome
        int[] chromosome = individual.getChromosome();
        // Get fitness
        bot.setGene(chromosome);
        bot.run(param);
//        bot=bot.run(param);
        float fitness = bot.distOverMoves(new Point2D.Float(1,1));

        // Store fitness
        individual.setFitness(fitness);

        return fitness;
    }

    /**
     * Evaluate the whole population
     *
     * Essentially, loop over the individuals in the population, calculate the
     * fitness for each, and then calculate the entire population's fitness. The
     * population's fitness may or may not be important, but what is important
     * here is making sure that each individual gets evaluated.
     *
     * The difference between this method and the one in chapter2 is that this
     * method requires the maze itself as a parameter; unlike the All Ones
     * problem in chapter2, we can't determine a fitness just by looking at the
     * chromosome -- we need to evaluate each member against the maze.
     *
     * @param population
     *            the population to evaluate
     * @param controller
     *            the maze to evaluate each individual against.
     */
    public void evalPopulation(Population population, RobotController controller) {
        double populationFitness = 0;

        // Loop over population evaluating individuals and suming population
        // fitness
        int i=0;
        for (Individ individual : population.getIndivids()) {
            populationFitness += this.calcFitness(individual, controller.newBot(controller.robots.get(i++)),controller.getParam());
        }

        population.setPopulationFitness(populationFitness);
    }

    /**
     * Check if population has met termination condition
     *
     * We don't actually know what a perfect solution looks like for the robot
     * controller problem, so the only constraint we can give to the genetic
     * algorithm is an upper bound on the number of generations.
     * @return boolean True if termination condition met, otherwise, false
     */
    public boolean isTerminationConditionMet( int generations,int max) {
            return generations > max;
        //    return (population.getFittest(0).getFitness() <=-5f);
    }

    /**
     * Selects parent for crossover using tournament selection
     *
     * Tournament selection works by choosing N random individuals, and then
     * choosing the best of those.
     * @return The individual selected as a parent
     */
    public Individ selectParent(Population population) {
        // Create tournament
        Population tournament = new Population(this.tournamentSize);

        // Add random individuals to the tournament
        population.shuffle();
        for (int i = 0; i < this.tournamentSize; i++) {
            Individ tournamentIndividual = population.getIndivid(i);
            tournament.setIndivid(i, tournamentIndividual);
        }

        // Return the best
        return tournament.getFittest(0);
    }

    /**
     * Apply mutation to population
     *
     * This method is the same as chapter2's version.
     *
     * @param population
     *            The population to apply mutation to
     * @return The mutated population
     */
    public Population mutatePopulation(Population population) {
        // Initialize new population
        Population newPopulation = new Population(this.populationSize);

        // Loop over current population by fitness
        for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {
            Individ individual = population.getFittest(populationIndex);
          /*  if (populationIndex >= this.elitismCount) {
                individual.setChromosome(mutate(individual));

            }*/

            // Loop over individual's genes
            for (int geneIndex = 0; geneIndex < individual.getChromosomeLength(); geneIndex++) {
                // Skip mutation if this is an elite individual
                if (populationIndex >= this.elitismCount) {
                    // Does this gene need mutation?
                    if (this.mutationRate > random.nextFloat( )) {
                        // Get new gene
                        int newGene = 1;
                        if (individual.getGene(geneIndex) == 1) {
                            newGene = 0;
                        }
                        // Mutate gene
                        individual.setGene(geneIndex, newGene);
                    }
                }
            }



            // Add individual to population
            newPopulation.setIndivid(populationIndex, individual);
        }

        // Return mutated population
        return newPopulation;
    }

    public int[] mutate(Individ parent1) {
        int index = this.random.nextInt(parent1.getChromosomeLength());
        int outerOffset = index >> SHIFT_FOR_DIVISION;
        int innerOffset = (index & MASK_FOR_MOD);
        int mask = 1 << innerOffset;
        int[] genom=parent1.getChromosome();
        genom[outerOffset] ^= mask;
        return genom;
    }

    /**
     * Crossover population using single point crossover
     *
     * Single-point crossover differs from the crossover used in chapter2.
     * Chapter2's version simply selects genes at random from each parent, but
     * in this case we want to select a contiguous region of the chromosome from
     * each parent.
     *
     * For instance, chapter2's version would look like this:
     *
     * Parent1: AAAAAAAAAA
     * Parent2: BBBBBBBBBB
     * Child  : AABBAABABA
     *
     * This version, however, might look like this:
     *
     * Parent1: AAAAAAAAAA
     * Parent2: BBBBBBBBBB
     * Child  : AAAABBBBBB
     *
     * @param population
     *            Population to crossover
     * @return Population The new population
     */
    public Population crossoverPopulation(Population population) {
        // Create new population
        Population newPopulation = new Population(population.size());

        // Loop over current population by fitness
        for (int populationIndex = 0; populationIndex < population.size(); populationIndex++) {
            Individ parent1 = population.getFittest(populationIndex);

            // Apply crossover to this individual?
            if (this.crossoverRate > Math.random() && populationIndex >= this.elitismCount) {
                // Initialize offspring
                Individ offspring = new Individ(parent1.getChromosomeLength());

                // Find second parent
                Individ parent2 = this.selectParent(population);

                // Get random swap point
                int swapPoint = (int) (Math.random() * (parent1.getChromosomeLength() + 1));

                // Loop over genome
                for (int geneIndex = 0; geneIndex < parent1.getChromosomeLength() ; geneIndex++) {
                    // Use half of parent1's genes and half of parent2's genes
                    if (geneIndex < swapPoint) {
                        offspring.setGene(geneIndex, parent1.getGene(geneIndex));
                    } else { //!!!
                        if(parent2.getChromosomeLength()<parent1.getChromosomeLength())
                            offspring.setGene(geneIndex, parent2.getGene(parent2.getChromosomeLength()-geneIndex));
                        else
                        offspring.setGene(geneIndex, parent2.getGene(geneIndex));
                    }
                }

                // Add offspring to new population
                newPopulation.setIndivid(populationIndex, offspring);
            } else {
                // Add individual to new population without applying crossover
                newPopulation.setIndivid(populationIndex, parent1);
            }
        }

        return newPopulation;
    }

    public Population newCrossover(Population population){
        Population newPopulation = new Population(population.size());

        // Loop over current population by fitness
        for (int populationIndex = 0; populationIndex <=  population.size()/2; populationIndex++) {
            Individ parent1 = population.getFittest(populationIndex);

            // Find second parent
            Individ parent2 = this.selectParent(population);
            List<Individ> children= cross(parent1,parent2,populationSize);
         //   children.get(0).setFitness(parent1.getFitness());
            newPopulation.setIndivid(populationIndex, children.get(0));
       //     children.get(1).setFitness(parent2.getFitness());
            newPopulation.setIndivid(population.size()/2+populationIndex-1 , children.get(1));
            if(population.size()%2 == 1){
                newPopulation.setIndivid(population.size() -1  , children.get(1));
            }
        }
        return newPopulation;
    }

    //for Vector2f
    public List<Individ> cross(Individ parent1,Individ parent2,int size){
        int[] genom1=parent1.getChromosome();
        int[] genom2=parent2.getChromosome();

        for (int outerOffset = 0; outerOffset < size; outerOffset++) {
            int mask = this.random.nextInt();
            int swapMask = (genom1[outerOffset] ^ genom2[outerOffset]) & mask;

            genom1[outerOffset] ^= swapMask;
            genom2[outerOffset] ^= swapMask;
        }
        Individ offspring1=new Individ(genom1);
        Individ offspring2=new Individ(genom2);
        List<Individ> offspring=new ArrayList<>();
        offspring.add(offspring1);
        offspring.add(offspring2);
        return offspring;
        /*
        int index1 = this.random.nextInt(populationSize);
        int index2 = this.random.nextInt(populationSize);
        int startIndex = Math.min(index1, index2);
        int endIndex = Math.max(index1, index2);
        int startOuterOffset = startIndex >> SHIFT_FOR_DIVISION;
        int startInnerOffset = OCTET_LENGTH - (startIndex & MASK_FOR_MOD);
        int endOuterOffset = endIndex >> SHIFT_FOR_DIVISION;
        int endInnerOffset = OCTET_LENGTH - (endIndex & MASK_FOR_MOD);
        long tmp = 0;
        int[] genom1=parent1.getChromosome();
        int[] genom2=parent2.getChromosome();
        if (startInnerOffset < OCTET_LENGTH-1) {
            long mask = 1L << (startInnerOffset + 1) - 1;
            long swapMask =  (parent1.getChromosome()[startOuterOffset] ^ parent2.getChromosome()[startOuterOffset]) & mask;
            genom1[startOuterOffset] ^= swapMask;
            genom2[startOuterOffset] ^= swapMask;
            startOuterOffset++;
        }
        for (int i=startOuterOffset;i<=endOuterOffset;i++){
            tmp = genom1[i];
            genom1[i] = genom2[i];
            genom2[i] = (int) tmp;
        }
        if (endInnerOffset > 0) {
            long mask = 1L << endInnerOffset - 1;
            long swapMask =  (genom1[endOuterOffset] ^ genom2[endOuterOffset]) & mask;
            genom1[endOuterOffset] ^= swapMask;
            genom2[endOuterOffset] ^= swapMask;
        }
        parent1.setChromosome(genom1);
        parent2.setChromosome(genom2);

    */
    }

    public Point2D getFittestRobotLocation(Population population,RobotController controller){
        Individ fittest = population.getFittest(populationSize-1);
        float fit= (float) calcFitness(fittest,controller.robots.get(0),controller.getParam());
        return controller.robots.get(0).showLocation();
    }

}
