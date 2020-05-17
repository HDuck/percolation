import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

interface Render {
    void grid(String title);
}

public class Percolation {
    private static final int TOP_BASE_SITE_ID = 0;
    private final boolean[][] grid;
    private int openSitesCount = 0;
    private final int bottomBaseSiteId;
    private final int gridSize;
    private final WeightedQuickUnionUF uf;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }

        gridSize = n;
        int row = 1;
        int col = 1;
        int id = 1;
        grid = new boolean[n + 1][n + 1];
        uf = new WeightedQuickUnionUF((n * n) + 2);

        while (row <= n) {
            while (col <= n) {
                grid[row][col] = false;
                col++;
                id++;
            }
            col = 1;
            row++;
        }

        bottomBaseSiteId = id;
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (row <= 0 || row > gridSize || col <= 0 || col > gridSize) {
            throw new IllegalArgumentException();
        }
        if (isOpen(row, col)) {
            return;
        }
        grid[row][col] = true;
        openSitesCount++;
        unionBoundarySites(row, col);
    }

    private int getGridSiteId(int row, int col) {
        return (row - 1) * gridSize + col;
    }

    private void unionBoundarySites(int row, int col) {
        int siteId = getGridSiteId(row, col);

        if (row == 1) {
            uf.union(TOP_BASE_SITE_ID, siteId);
        }
        else if (isOpen(row - 1, col)) {
            int topSiteId = getGridSiteId(row - 1, col);
            uf.union(siteId, topSiteId);
        }

        if (row == gridSize) {
            uf.union(bottomBaseSiteId, siteId);
        }
        else if (isOpen(row + 1, col)) {
            int bottomSiteId = getGridSiteId(row + 1, col);
            uf.union(siteId, bottomSiteId);
        }

        if (col > 1 && isOpen(row, col - 1)) {
            int leftSiteId = getGridSiteId(row, col - 1);
            uf.union(siteId, leftSiteId);
        }

        if (col < gridSize && isOpen(row, col + 1)) {
            int rightSiteId = getGridSiteId(row, col + 1);
            uf.union(siteId, rightSiteId);
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (row <= 0 || row > gridSize || col <= 0 || col > gridSize) {
            throw new IllegalArgumentException();
        }
        return grid[row][col];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (row <= 0 || row > gridSize || col <= 0 || col > gridSize) {
            throw new IllegalArgumentException();
        }
        int siteId = getGridSiteId(row, col);
        return uf.find(siteId) == topBaseRootSiteId();
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openSitesCount;
    }

    private int topBaseRootSiteId() {
        return uf.find(TOP_BASE_SITE_ID);
    }

    private int bottomBaseRootSiteId() {
        return uf.find(bottomBaseSiteId);
    }

    // does the system percolate?
    public boolean percolates() {
        return bottomBaseRootSiteId() == topBaseRootSiteId();
    }

    // test client (optional)
    public static void main(String[] args) {
        int gridSize = Integer.parseInt(args[0]);
        Percolation perc = new Percolation(gridSize);

        Render render = (String title) -> {
            StdOut.println(title);
            int row = 1;
            for (int i = 0; i <= gridSize * 2; i++) {
                int col = 1;
                for (int j = 0; j <= gridSize * 2; j++) {
                    if (i % 2 == 0) {
                        if (j % 2 == 0) {
                            StdOut.print("+");
                        }
                        else {
                            StdOut.print("-");
                        }
                    }
                    else if (j % 2 == 0) {
                        StdOut.print("|");
                    }
                    else {
                        if (perc.isFull(row, col)) {
                            StdOut.print("@");
                        }
                        else if (perc.isOpen(row, col)) {
                            StdOut.print(" ");
                        }
                        else {
                            StdOut.print("#");
                        }
                        col++;
                    }
                }
                if (i % 2 != 0) {
                    row++;
                }
                StdOut.println();
            }
        };

        int iteration = 1;
        while (!perc.percolates()) {
            if (iteration % (gridSize * 2) == 0) {
                StdOut.println();
                render.grid(String.format("Iteration #%s", iteration));
            }

            int randomRow;
            int randomCol;
            do {
                randomCol = StdRandom.uniform(1, gridSize + 1);
                randomRow = StdRandom.uniform(1, gridSize + 1);
            } while (perc.isOpen(randomRow, randomCol));

            perc.open(randomRow, randomCol);
            iteration++;
        }

        render.grid(String.format("Percolated! Iteration #%s", iteration));
    }
}
