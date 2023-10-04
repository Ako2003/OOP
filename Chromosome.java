import java.util.Random;

public class Chromosome {
    private int[] genes;
    private double fitness;

    public Chromosome() {
        // Initialize the genes array
        genes = new int[40];
    }

    public void initialize() {
        // Initialize each gene with a random value
        Random random = new Random();
        for (int i = 0; i < genes.length; i++) {
            genes[i] = random.nextInt(/* maximum value for the gene */);
        }
    }

    public int[] getGenes() {
        return genes;
    }

    public void setGenes(int[] genes) {
        this.genes = genes;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
}
