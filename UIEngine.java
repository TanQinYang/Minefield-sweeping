
/**
 * UIEngine follows original Minefield/Cell flow:
 * first click -> createMines, evaluateField, revealStartingArea; later -> guess.
 */
public class UIEngine {
    private final Minefield mf;
    private final int rows, cols, mines;
    private boolean firstClickDone = false;

    public UIEngine(int rows, int cols, int mines) {
        this.rows = rows;
        this.cols = cols;
        this.mines = Math.max(1, Math.min(mines, rows * cols - 1));
        this.mf = new Minefield(rows, cols, this.mines);
    }

    public int rows() { return rows; }
    public int cols() { return cols; }
    public boolean isFirstClickDone() { return firstClickDone; }
    public int flagsRemaining() { return mf.getFlagsRemaining(); }
    public boolean isGameOver() { return mf.gameOver(); }

    public boolean isWin() {
        if (!mf.gameOver()) return false;
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++) {
                Cell cell = mf.getCell(r, c);
                if (cell != null && cell.getRevealed() && "M".equals(cell.getStatus())) return false;
            }
        return true;
    }

    /** @return true if a mine was hit by this reveal. */
    public boolean reveal(int r, int c) {
        if (!firstClickDone) {
            mf.createMines(r, c, mines);
            mf.evaluateField();
            mf.revealStartingArea(r, c);
            firstClickDone = true;
            return false;
        }
        return mf.guess(r, c, false);
    }

    public void toggleFlag(int r, int c) { mf.guess(r, c, true); }

    public ViewCell viewCell(int r, int c) {
        Cell cell = mf.getCell(r, c);
        if (cell == null) return new ViewCell(false, false, false, 0);
        String status = cell.getStatus();
        boolean digit = status.length() == 1 && Character.isDigit(status.charAt(0));
        int adj = digit ? (status.charAt(0) - '0') : 0;
        boolean flagged = "F".equals(status);
        boolean mine = "M".equals(status);
        boolean revealed = cell.getRevealed();
        if (isGameOver() && mine) return new ViewCell(true, true, false, adj);
        return new ViewCell(revealed, mine, flagged, adj);
    }

    public static class ViewCell {
        public final boolean revealed, isMine, flagged;
        public final int adj;
        public ViewCell(boolean revealed, boolean isMine, boolean flagged, int adj) {
            this.revealed = revealed; this.isMine = isMine; this.flagged = flagged; this.adj = adj;
        }
    }
}
