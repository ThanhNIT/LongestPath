import ui.LifeUserInterface;
import ui.UIAuxiliaryMethods;
import ui.UserInterfaceFactory;

import java.util.Scanner;

public class Life {

    LifeUserInterface ui;
    final static int WIDTH = 9, HEIGHT = 9;
    int maxAllowedGenerations = 0;
    int oscillateCheck = 0;
    char[][] initial = new char[WIDTH][HEIGHT];
    boolean die = false;
    boolean oscillate = false;
    int period = 0;

    void start() {
        inputScanner();
        char[][][] board = new char[maxAllowedGenerations][WIDTH][HEIGHT];
        board = getInitialBoard(maxAllowedGenerations, board);
        ui = UserInterfaceFactory.getLifeUI(WIDTH, HEIGHT);
        predictGeneration(board);
        if (!die && !oscillate) {
            ui.printf("Maximum number of generations exceeded");
        }
    }

    public void predictGeneration(char[][][] board) {
        for (int generation = 0; generation < maxAllowedGenerations; generation++) {
            ui.wait(1000);
            die = true;

            for (int i = 0; i < WIDTH; i++) {
                for (int j = 0; j < HEIGHT; j++) {
                    int alive = liveneg(i, j, generation, board);
                    if (board[generation][i][j] == 'x' && alive < 2) {
                        board[generation + 1][i][j] = ' ';
                    } else if (board[generation][i][j] == 'x' && alive > 3) {
                        board[generation + 1][i][j] = ' ';
                    } else if (board[generation][i][j] == ' ' && alive == 3) {
                        board[generation + 1][i][j] = 'x';
                    } else if (board[generation][i][j] == 'x' && (alive == 3 || alive == 2)) {
                        board[generation + 1][i][j] = 'x';
                    }

                    if (board[generation][i][j] == 'x')
                        ui.place(j, i, LifeUserInterface.ALIVE);
                    else
                        ui.place(j, i, LifeUserInterface.DEAD);
                }
            }

            checkOscillator(generation, board);
            ui.showChanges();
            checkDied(generation, board);

            if (die) {
                ui.printf("Figure died");
                return;
            }

            if (oscillate) {
                if (period > 1) {
                    ui.printf("Oscillator with period " + period);

                } else if (period == 1) {
                    ui.printf("Still figure");
                }
                return;
            }
        }
    }

    private int liveneg(int x, int y, int generation, char[][][] board) {
        int alive = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                int negx = x + i;
                int negy = y + j;
                if (negx >= 0 && negx < WIDTH && negy >= 0 && negy < WIDTH) {
                    if (board[generation][negx][negy] == 'x') {
                        alive++;
                    }
                }
            }
        }
        return alive;
    }

    private void checkDied(int generation, char[][][] board) {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (board[generation][i][j] == 'x') {
                    die = false;
                    return;
                }
            }
        }
    }


    private boolean areSame(char A[][], char B[][]) {
        int i, j;
        for (i = 0; i < WIDTH; i++)
            for (j = 0; j < HEIGHT; j++)
                if (A[i][j] != B[i][j])
                    return false;
        return true;
    }

    private void checkOscillator(int generation, char[][][] board) {

        if (generation < oscillateCheck) {
            for (int g = 0; g < generation; g++) {
                if (areSame(board[generation], board[g])) {
                    oscillate = true;
                    period = generation - g;
                    return;
                }
            }
        } else {
            for (int g = generation - oscillateCheck; g < generation; g++) {
                if (areSame(board[generation], board[g])) {
                    oscillate = true;
                    period = generation - g;
                    return;
                }
            }
        }
    }

    private char[][][] getInitialBoard(int maxAllowedGenerations, char[][][] board) {
        board = new char[maxAllowedGenerations][WIDTH][HEIGHT];
        for(int g=0;g<maxAllowedGenerations;g++) {
            for (int i = 0; i < WIDTH; i++) {
                for (int j = 0; j < HEIGHT; j++) {
                   board[g][i][j]=' ';
                }
            }
        }
        board[0] = initial;

        return board;
    }

    private int getNumberOfNeighbours(int x, int y, int generation, char[][][] board) {
        int numberOfNeighbours = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (board[generation][x + i][y + j] == 'x') {
                    numberOfNeighbours += 1;
                }
            }
        }

        if (board[generation][x][y] == 'x') {
            numberOfNeighbours -= 1;
        }

        return numberOfNeighbours;

    }

    void inputScanner() {
        Scanner input = UIAuxiliaryMethods.askUserForInput().getScanner();
        readBoard(input);
    }

    void readBoard(Scanner boardInput) {
        Scanner startScanner = new Scanner(boardInput.next());
        maxAllowedGenerations = startScanner.nextInt() +1;
        Scanner pScanner = new Scanner(boardInput.next());
        oscillateCheck = pScanner.nextInt();
        startScanner.close();
        pScanner.close();

        int i = 0;
        boardInput.nextLine();
        while (boardInput.hasNextLine() && i < 9) {
            String line = (boardInput.nextLine());
            initial[i] = line.toCharArray();
            i++;
        }
    }

    public static void main(String[] args) {
        new Life().start();
    }
}