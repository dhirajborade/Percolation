
/**********************************************************************************
 *  Name: 		Dhiraj Vasant Borade
 *  Course: 	Algorithms
 *  University: Princeton University (Coursera)
 *  
 *  Compilation:  javac Percolation.java
 *  Execution:    java Percolation inputSize (n) indexOfSiteToBeOpened (row, col)
 *  Dependencies: None
 *
 *  This program takes grid size n and the row index and column index as the 
 *  command line arguments
 *  
 *  It follows the following procedure,
 *  	- Creates an n-by-n grid of sites (initially all blocked).
 *  	- Add extra two sites as topIndex and bottomIndex as virtual sites to 
 *  	  check connectivity between upper row and bottom row.
 *  	- open a site based on the input row index and column index and connect 
 *  	  it to its neighbor iff the neighbor is open, using union() method.
 *  	- Also if the site is in the top row connect it to the topIndex and is 
 *  	  the site is in the bottom row and connected to the topIndex, connect 
 *  	  it to the bottomIndex.
 *  	- check for variable percolates, if topIndex is connected to bottomIndex, 
 *  	  make it true and the system percolates *  
 *
 **********************************************************************************/

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

	private int rowLength; // size n
	private int gridSize; // size n * n
	private WeightedQuickUnionUF uf;
	private boolean[] siteStatus; // store the status of all the sites
	private int topIndex; // virtual site connected to all the sites in the upper row
	private int bottomIndex; // virtual site connected to all the sites in the bottom row
	private int tempRow;
	private int tempCol;
	private int siteIndex;
	private boolean percolates;

	// create n-by-n grid, with all sites blocked
	public Percolation(int n) {
		if (n <= 0) {
			throw new java.lang.IllegalArgumentException("n must be larger than 0");
		}
		this.rowLength = n;
		this.gridSize = n * n;
		this.uf = new WeightedQuickUnionUF(this.gridSize + 2); // 2 virtual sites
		this.siteStatus = new boolean[this.gridSize];
		this.topIndex = this.gridSize;
		this.bottomIndex = this.gridSize + 1;
	}

	// check whether row and column index are within bounds
	private void checkInputIndex(int row, int col) {
		if (0 >= row || this.rowLength < row) {
			throw new java.lang.IndexOutOfBoundsException("row index out of bounds");
		}
		if (0 >= col || this.rowLength < col) {
			throw new java.lang.IndexOutOfBoundsException("column index out of bounds");
		}
	}

	// each site's index is converted from (row, col) to 0 to (N^2 - 1)
	private int getSiteIndex(int row, int col) {
		// change indexes to start from 0 and not 1
		this.tempRow = row - 1;
		this.tempCol = col - 1;
		this.siteIndex = this.tempRow * this.rowLength + this.tempCol;
		return this.siteIndex;
	}

	// get the index of the neighbor in the specified direction
	private int getNeighborIndex(int row, int col, int direction) {
		if (direction == 0) {
			if (row == 1) {
				return -1;
			}
			return getSiteIndex(row - 1, col);
		} else if (direction == 1) {
			if (col == this.rowLength) {
				return -1;
			}
			return getSiteIndex(row, col + 1);
		} else if (direction == 2) {
			if (row == this.rowLength) {
				return -1;
			}
			return getSiteIndex(row + 1, col);
		} else if (direction == 3) {
			if (col == 1) {
				return -1;
			}
			return getSiteIndex(row, col - 1);
		}
		return -1;
	}

	// open site (row, col) if it is not open already
	public void open(int row, int col) {
		checkInputIndex(row, col);
		int siteIndex = getSiteIndex(row, col);
		if (!this.siteStatus[siteIndex]) {
			this.siteStatus[siteIndex] = true;
			int neighborIndex;
			boolean hasNeighbor = false;
			for (int i = 0; i < 4; i++) {
				neighborIndex = this.getNeighborIndex(row, col, i);
				if (neighborIndex != -1 && this.isOpen(neighborIndex)) {
					uf.union(siteIndex, neighborIndex);
					hasNeighbor = true;
				}
			}

			if (row == 1) {
				uf.union(siteIndex, this.topIndex);
			}

			if (hasNeighbor) {
				for (int i = this.gridSize - 1; i >= this.gridSize - this.rowLength; i--) {
					if (this.isOpen(i) && uf.connected(i, this.topIndex)) {
						uf.union(i, this.bottomIndex);
						break;
					}
				}
			} else if (this.gridSize == 1) {
				uf.union(siteIndex, this.bottomIndex);
			}
		}

	}

	// is site (row, col) open?
	public boolean isOpen(int row, int col) {
		checkInputIndex(row, col);
		int siteIndex = this.getSiteIndex(row, col);
		return this.isOpen(siteIndex);
	}

	private boolean isOpen(int index) {
		return this.siteStatus[index];
	}

	// is site (row, col) full?
	public boolean isFull(int row, int col) {
		checkInputIndex(row, col);
		int siteIndex = getSiteIndex(row, col);
		if (!this.siteStatus[siteIndex]) {
			return false;
		}
		if (uf.connected(this.topIndex, siteIndex)) {
			return true;
		}
		return false;

	}

	// number of open sites
	public int numberOfOpenSites() {
		int count = 0;
		for (int i = 0; i < this.siteStatus.length; i++) {
			if (this.siteStatus[i] == true) {
				count++;
			}
		}
		return count;
	}

	// does the system percolate?
	public boolean percolates() {
		if (!this.percolates) {
			this.percolates = uf.connected(this.topIndex, this.bottomIndex);
		}
		return this.percolates;
	}

	// test client (optional)
	public static void main(String[] args) {
		StdOut.println("Enter the row length: ");
		int n = StdIn.readInt();
		Percolation perc = new Percolation(n);
		while (!perc.percolates()) {
			StdOut.println("Enter the site co-ordinates to be openend");
			StdOut.println("X - Coordinate: ");
			int x_coordinate = StdIn.readInt();
			StdOut.println("Y - Coordinate: ");
			int y_coordinate = StdIn.readInt();
			perc.open(x_coordinate, y_coordinate);
		}
		if (perc.percolates()) {
			StdOut.println("System percolates");
		}
	}
}