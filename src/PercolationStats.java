import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

	private int rowLength;
	private int numberOfTrials;
	private double[] percolationThresholds;

	// perform trials independent experiments on an n-by-n grid
	public PercolationStats(int n, int trials) {
		if (n <= 0) {
			throw new java.lang.IllegalArgumentException("n is out of bound");
		}
		if (trials <= 0) {
			throw new java.lang.IllegalArgumentException("trials is out of bound");
		}
		this.rowLength = n;
		this.numberOfTrials = trials;
		this.percolationThresholds = new double[this.numberOfTrials];

		for (int i = 0; i < this.numberOfTrials; i++) {
			this.percolationThresholds[i] = this.calculatePercolationThreshold();
		}
	}

	private double calculatePercolationThreshold() {
		Percolation myPerc = new Percolation(this.rowLength);
		int openedSitesCount = 0;
		int row, col;
		while (!myPerc.percolates()) {
			do {
				row = StdRandom.uniform(this.rowLength) + 1;
				col = StdRandom.uniform(this.rowLength) + 1;
			} while (myPerc.isOpen(row, col));
			openedSitesCount++;
			myPerc.open(row, col);
		}
		return openedSitesCount / Math.pow(this.rowLength, 2);
	}

	// sample mean of percolation threshold
	public double mean() {
		return StdStats.mean(this.percolationThresholds);

	}

	// sample standard deviation of percolation threshold
	public double stddev() {
		if (this.numberOfTrials == 1) {
			return Double.NaN;
		}
		return StdStats.stddev(percolationThresholds);

	}

	// low endpoint of 95% confidence interval
	public double confidenceLo() {
		return this.mean() - (1.96 * this.stddev() / Math.sqrt(this.numberOfTrials));

	}

	// high endpoint of 95% confidence interval
	public double confidenceHi() {
		return this.mean() + (1.96 * this.stddev() / Math.sqrt(this.numberOfTrials));

	}

	// test client (described below)
	public static void main(String[] args) {
		int n = StdIn.readInt();
		int trials = StdIn.readInt();
		PercolationStats myStats = new PercolationStats(n, trials);
		StdOut.println("mean 			= " + myStats.mean());
		StdOut.println("stddev 			= " + myStats.stddev());
		StdOut.println("95% confidence interval = [" + myStats.confidenceLo() + ", " + myStats.confidenceHi() + "]");
	}
}