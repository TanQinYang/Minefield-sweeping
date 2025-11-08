// Import Section
import java.util.Random;

public class Minefield {
    /**
     * Global Section
     */

    /*
     * Class Variable Section
     *
     */
    private Cell[][] field;
    private int rows;
    private int columns;
    private int flagsRemaining;
    private int mines;
    private boolean debugMode;
    /*Things to Note:
     * Please review ALL files given before attempting to write these functions.
     * Understand the Cell.java class to know what object our array contains and what methods you can utilize
     * Understand the StackGen.java interface to know what type of stack you will be working with and methods you can utilize
     * Understand the QGen.java interface to know what type of queue you will be working with and methods you can utilize
     */

    /**
     * Minefield
     * <p>
     * Build a 2-d Cell array representing your minefield.
     * Constructor
     *
     * @param rows    Number of rows.
     * @param columns Number of columns.
     * @param flags   Number of flags, should be equal to mines
     */
    public Minefield(int rows, int columns, int flags) {
        this.rows = rows;
        this.columns = columns;
        this.flagsRemaining = flags;
        this.field = new Cell[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                field[i][j] = new Cell(false, "-");
            }
        }
    }


    /**
     * evaluateField
     *
     * @function: Evaluate entire array.
     * When a mine is found check the surrounding adjacent tiles. If another mine is found during this check, increment adjacent cells status by 1.
     */
    public void evaluateField() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Cell cell = field[i][j];
                if (cell.getStatus().equals("M")) {
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            if (dx == 0 && dy == 0) continue;
                            int ni = i + dx;
                            int nj = j + dy;
                            if (ni >= 0 && ni < rows && nj >= 0 && nj < columns) {
                                Cell neighbor = field[ni][nj];
                                if (!neighbor.getStatus().equals("M")) {
                                    String status = neighbor.getStatus();
                                    if (status.equals("-")) {
                                        neighbor.setStatus("1");
                                    } else if (!status.equals("F")) {
                                        try {
                                            int num = Integer.parseInt(status) + 1;
                                            neighbor.setStatus(Integer.toString(num));
                                        } catch (NumberFormatException e) {
                                            // should not happen
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * createMines
     * <p>
     * Randomly generate coordinates for possible mine locations.
     * If the coordinate has not already been generated and is not equal to the starting cell set the cell to be a mine.
     * utilize rand.nextInt()
     *
     * @param x     Start x, avoid placing on this square.
     * @param y     Start y, avoid placing on this square.
     * @param mines Number of mines to place.
     */
    public void createMines(int x, int y, int mines) {
        Random rand = new Random();
        int placed = 0;
        while (placed < mines) {
            int randX = rand.nextInt(rows);
            int randY = rand.nextInt(columns);
            if (randX == x && randY == y) continue;
            Cell cell = field[randX][randY];
            if (cell.getStatus().equals("M")) continue;
            if (cell.getRevealed()) continue;
            cell.setStatus("M");
            placed++;
        }
        this.mines = mines;
    }


    /**
     * guess
     * <p>
     * Check if the guessed cell is inbounds (if not done in the Main class).
     * Either place a flag on the designated cell if the flag boolean is true or clear it.
     * If the cell has a 0 call the revealZeroes() method or if the cell has a mine end the game.
     * At the end reveal the cell to the user.
     *
     * @param x    The x value the user entered.
     * @param y    The y value the user entered.
     * @param flag A boolean value that allows the user to place a flag on the corresponding square.
     * @return boolean Return false if guess did not hit mine or if flag was placed, true if mine found.
     */
    public boolean guess(int x, int y, boolean flag) {
        if (x < 0 || x >= rows || y < 0 || y >= columns) return false;
        Cell cell = field[x][y];
        if (flag) {
            if (flagsRemaining <= 0) return false;
            if (cell.getRevealed()) return false;
            if (cell.getStatus().equals("F")) {
                cell.setStatus("-");
                flagsRemaining++;
            } else {
                cell.setStatus("F");
                flagsRemaining--;
            }
            return false;
        } else {
            if (cell.getStatus().equals("M")) {
                cell.setRevealed(true);
                return true;
            }
            if (cell.getStatus().equals("F")) return false;
            cell.setRevealed(true);
            if (cell.getStatus().equals("0")) {
                revealZeroes(x, y);
            }
            return false;
        }
    }

    /**
     * gameOver
     * <p>
     * Ways a game of Minesweeper ends:
     * 1. player guesses a cell with a mine: game over -> player loses
     * 2. player has revealed the last cell without revealing any mines -> player wins
     *
     * @return boolean Return false if game is not over and squares have yet to be revealed, otherwise return true.
     */
    public boolean gameOver() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Cell cell = field[i][j];
                if (cell.getStatus().equals("M") && cell.getRevealed()) {
                    return true;
                }
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Cell cell = field[i][j];
                if (!cell.getStatus().equals("M") && !cell.getRevealed()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Reveal the cells that contain zeroes that surround the inputted cell.
     * Continue revealing 0-cells in every direction until no more 0-cells are found in any direction.
     * Utilize a STACK to accomplish this.
     * <p>
     * This method should follow the psuedo-code given in the lab writeup.
     * Why might a stack be useful here rather than a queue?
     *
     * @param x The x value the user entered.
     * @param y The y value the user entered.
     */
    public void revealZeroes(int x, int y) {
        Stack1Gen<int[]> stack = new Stack1Gen<>();
        stack.push(new int[]{x, y});
        while (!stack.isEmpty()) {
            int[] current = stack.pop();
            int currentX = current[0];
            int currentY = current[1];
            Cell cell = field[currentX][currentY];
            if (cell.getRevealed()) continue;
            cell.setRevealed(true);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue;
                    int nx = currentX + dx;
                    int ny = currentY + dy;
                    if (nx >= 0 && nx < rows && ny >= 0 && ny < columns) {
                        Cell neighbor = field[nx][ny];
                        if (!neighbor.getRevealed() && neighbor.getStatus().equals("0")) {
                            stack.push(new int[]{nx, ny});
                        }
                    }
                }
            }
        }
    }

    /**
     * revealStartingArea
     * <p>
     * On the starting move only reveal the neighboring cells of the initial cell and continue revealing the surrounding concealed cells until a mine is found.
     * Utilize a QUEUE to accomplish this.
     * <p>
     * This method should follow the psuedo-code given in the lab writeup.
     * Why might a queue be useful for this function?
     *
     * @param x The x value the user entered.
     * @param y The y value the user entered.
     */
    public void revealStartingArea(int x, int y) {
        Q1Gen<int[]> queue = new Q1Gen<>();
        queue.add(new int[]{x, y});
        boolean[][] visited = new boolean[rows][columns];
        visited[x][y] = true;
        while (queue.length() > 0) {
            int[] current = queue.remove();
            int currentX = current[0];
            int currentY = current[1];
            Cell cell = field[currentX][currentY];
            if (cell.getStatus().equals("M")) {
                break;
            }
            cell.setRevealed(true);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) continue;
                    int nx = currentX + dx;
                    int ny = currentY + dy;
                    if (nx >= 0 && nx < rows && ny >= 0 && ny < columns && !visited[nx][ny]) {
                        visited[nx][ny] = true;
                        queue.add(new int[]{nx, ny});
                    }
                }
            }
        }
    }

    /**
     * For both printing methods utilize the ANSI color codes provided!
     * <p>
     * <p>
     * <p>
     * <p>
     * <p>
     * debug
     *
     * @function This method should print the entire minefield, regardless if the user has guessed a square.
     * This method should print out when debug mode has been selected. It is very similar to the toString method below.
     */
    public void debug() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                System.out.print(field[i][j].getStatus() + " ");
            }
            System.out.println();
        }
    }

    /**
     * toString
     *
     * @return String The string that is returned only has the squares that has been revealed to the user or that the user has guessed.
     */
    @Override
public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < columns; j++) {
            Cell cell = field[i][j];
            if (cell.getRevealed()) {
                sb.append(cell.getStatus());
            } else if ("F".equals(cell.getStatus())) {
                sb.append("F");
            } else {
                sb.append("-");
            }
            sb.append(' ');
        }
        if (columns > 0) sb.setLength(sb.length() - 1);
        sb.append('\n');
    }
    return sb.toString();
}

public Cell getCell(int x, int y) {
    if (x >= 0 && x < rows && y >= 0 && y < columns) {
        return field[x][y];
    }
    return null;
}

public int getFlagsRemaining() { return flagsRemaining; }

public int getRows() { return rows; }

public int getColumns() { return columns; }
}