
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private static final int TOP_BASE_SITE_ID = 0;
    private final int[][] grid;
    private final boolean[] openSites;
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
        grid = new int[n + 1][n + 1];
        openSites = new boolean[(n * n) + 1];
        uf = new WeightedQuickUnionUF((n * n) + 2);

        while (row <= n) {
            while (col <= n) {
                grid[row][col] = id;
                openSites[id] = false;
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
        int siteId = getGridSiteId(row, col);
        openSites[siteId] = true;
        openSitesCount++;
        unionBoundarySites(row, col);
    }

    private int getGridSiteId(int row, int col) {
        return grid[row][col];
    }

    private void unionBoundarySites(int row, int col) {
        int siteId = getGridSiteId(row, col);

        if (row == 1) {
            uf.union(TOP_BASE_SITE_ID, siteId);
        } else if (isOpen(row - 1, col)) {
            int topSiteId = getGridSiteId(row - 1, col);
            uf.union(siteId, topSiteId);
        }

        if (row == gridSize) {
            uf.union(bottomBaseSiteId, siteId);
        } else if (isOpen(row + 1, col)) {
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
        int siteId = getGridSiteId(row, col);
        return openSites[siteId];
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
        Percolation perc = new Percolation(3);

        int[][] openPath = { { 3, 1 }, { 3, 3 }, { 1, 3 }, { 2, 3 } };

        for (int[] path : openPath) {
            int row = path[0];
            int col = path[1];
            perc.open(row, col);

            System.out.println(String.format("%s-%s opened", row, col));
            System.out.println(String.format("%s-%s percolates: %s", row, col, perc.percolates()));
            for (int[] path2 : openPath) {
                int row2 = path2[0];
                int col2 = path2[1];
                System.out.println(String.format("* %s-%s is full: %s", row2, col2, perc.isFull(row2, col2)));
            }
        }
    }
}
