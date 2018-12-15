package Genetic_FW;


public class Individ {
    private int[] chromosome;
    private double fitness = -1;

    /**
     * Initializes individual with specific chromosome
     */
    public Individ(int[] chromosome) {
        // Create individual chromosome
        this.chromosome = chromosome;
    }

    /**
     * Initializes random individual.
     *
     * This constructor assumes that the chromosome is made entirely of 0s and
     * 1s, which may not always be the case, so make sure to modify as
     * necessary. This constructor also assumes that a "random" chromosome means
     * simply picking random zeroes and ones, which also may not be the case
     * (for instance, in a traveling salesman problem, this would be an invalid
     * solution).
     *
     * @param chromosomeLength
     *            The length of the individuals chromosome
     */
    public Individ(int chromosomeLength) {

        this.chromosome = new int[chromosomeLength];
        for (int gene = 0; gene < chromosomeLength; gene++) {
            if (0.5 < Math.random()) {
                this.setGene(gene, 1);
            } else {
                this.setGene(gene, 0);
            }
        }

    }

    /**
     * Gets individual's chromosome
     */
    public int[] getChromosome() {
        return this.chromosome;
    }

    /**
     * Gets individual's chromosome length
     */
    public int getChromosomeLength() {
        return this.chromosome.length;
    }

    /**
     * Set gene at offset
     *
     * @param gene
     * @param offset
     * @return gene
     */
    public void setGene(int offset, int gene) {
        this.chromosome[offset] = gene;
    }

    public void setChromosome(int[] chromosome){
//       if(chromosome.equals(this.chromosome))
//           System.out.println("same??");
        this.chromosome=chromosome;
    }

    /**
     * Get gene at offset
     */
    public int getGene(int offset) {
        return this.chromosome[offset];
    }

    /**
     * Store individual's fitness
     */
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    /**
     * Gets individual's fitness
     */
    public double getFitness() {
        return this.fitness;
    }


    /**
     * Display the chromosome as a string.
     */
    public String toString() {
        String output = "";
        for (int gene = 0; gene < this.chromosome.length; gene++) {
            output += this.chromosome[gene];
        }
        return output;
    }
}
