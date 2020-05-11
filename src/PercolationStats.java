import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.StdOut;

public class PercolationStats {
    private static final double CONFIDENCE_COOF = 1.96;
    private final double[] percolationThresholds;
    private final double trialsSqrt;
    private double cachedMean;
    private double cachedStddev;
    
    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException();
        }
        
        percolationThresholds = new double[trials];
        trialsSqrt = Math.sqrt(trials);

        for (int i = 0; i < trials; i++) {
            Percolation perc = new Percolation(n);

            while (!perc.percolates()) {
                int randomCol = StdRandom.uniform(1, n + 1);
                int randomRow = StdRandom.uniform(1, n + 1);
                perc.open(randomRow, randomCol);
            }

            int sites = n * n;
            int openSites = perc.numberOfOpenSites();
            percolationThresholds[i] = (double) openSites / (double) sites;
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        if (cachedMean == 0) {
            cachedMean = StdStats.mean(percolationThresholds);
        }
        return cachedMean;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        if (cachedStddev == 0) {
            cachedStddev = StdStats.stddev(percolationThresholds);
        }
        return cachedStddev;
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean() - ((CONFIDENCE_COOF * stddev()) / trialsSqrt);
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean() + ((CONFIDENCE_COOF * stddev()) / trialsSqrt);
    }

    // test client (see below)
    public static void main(String[] args) {
        int gridSize = Integer.parseInt(args[0].trim());
        int trials = Integer.parseInt(args[1].trim());
        
        PercolationStats percStats = new PercolationStats(gridSize, trials);
        
        StdOut.println("mean                    = " + percStats.mean());
        StdOut.println("stddev                  = " + percStats.stddev());
        StdOut.print("95% confidence interval = [");
        StdOut.print(percStats.confidenceLo());
        StdOut.print(", ");
        StdOut.print(percStats.confidenceHi());
        StdOut.println("]");
    }
}
