// LEGACY (terminal-only) runner — prefer MinesweeperApp GUI
//Import Section
import java.util.Random;
import java.util.Scanner;

/*
 * Provided in this class is the neccessary code to get started with your game's implementation
 * You will find a while loop that should take your minefield's gameOver() method as its conditional
 * Then you will prompt the user with input and manipulate the data as before in project 2
 * 
 * Things to Note:
 * 1. Think back to Project 1 when we asked our user to give a shape. In this project we will be asking the user to provide a mode. Then create a minefield accordingly
 * 2. You must implement a way to check if we are playing in debug mode or not.
 * 3. When working inside your while loop think about what happens each turn. We get input, user our methods, check their return values. repeat.
 * 4. Once while loop is complete figure out how to determine if the user won or lost. Print appropriate statement.
 */

public class Main {
    public static void main(String[] args) {
        System.out.println("[LEGACY] You are running the terminal version. For the GUI, run: java MinesweeperApp");

        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose difficulty (easy, medium, hard):");
        String difficulty = scanner.nextLine().trim().toLowerCase();
        int rows, columns, mines;
        switch (difficulty) {
            case "easy":
                rows = 5;
                columns = 5;
                mines = 5;
                break;
            case "medium":
                rows = 9;
                columns = 9;
                mines = 12;
                break;
            case "hard":
                rows = 20;
                columns = 20;
                mines = 40;
                break;
            default:
                System.out.println("Invalid difficulty. Defaulting to easy.");
                rows = 5;
                columns = 5;
                mines = 5;
        }
        System.out.println("Enable debug mode? (y/n)");
        boolean debug = scanner.nextLine().trim().equalsIgnoreCase("y");

        Minefield minefield = new Minefield(rows, columns, mines);

        System.out.println("Enter starting coordinates (x y):");
        int startX = scanner.nextInt();
        int startY = scanner.nextInt();
        scanner.nextLine();

        minefield.createMines(startX, startY, mines);
        minefield.evaluateField();
        minefield.revealStartingArea(startX, startY);

        while (!minefield.gameOver()) {
            if (debug) {
                minefield.debug();
            } else {
                System.out.println(minefield.toString());
            }
            System.out.println("Flags remaining: " + minefield.getFlagsRemaining());
            System.out.println("Enter coordinates and flag (x y f, where f is 1 for flag, 0 otherwise):");
            int x = scanner.nextInt();
            int y = scanner.nextInt();
            int f = scanner.nextInt();
            scanner.nextLine();
            boolean hitMine = minefield.guess(x, y, f == 1);
            if (hitMine) {
                break;
            }
        }

        if (minefield.gameOver()) {
            boolean won = true;
            for (int i = 0; i < minefield.getRows(); i++) {
                for (int j = 0; j < minefield.getColumns(); j++) {
                    Cell cell = minefield.getCell(i, j);
                    if (cell.getStatus().equals("M") && cell.getRevealed()) {
                        won = false;
                        break;
                    }
                }
                if (!won) break;
            }
            if (won) {
                System.out.println("Congratulations! You won!");
            } else {
                System.out.println("You hit a mine! Game over.");
            }
        }
        scanner.close();
    }
}
