package sg.edu.ntu.scse.mdp13.pathFinder;

import android.util.Log;

import sg.edu.ntu.scse.mdp13.pathFinder.BFS;
import sg.edu.ntu.scse.mdp13.pathFinder.DFS;

public class PathFinder {
    int startX = 1;
    int startY = 1;
    int endX = 15;
    int endY = 15;
    int obstacleX = 8;
    int obstacleY = 8;

    private int rows = 16;
    private int cols = 16;

    int[][] board = new int[rows][cols];

    public static final int EMPTY_CELL_CODE = 0;
    public static final int START_CELL_CODE = 1;
    public static final int OBSTACLE_CELL_CODE = 2;
    public static final int END_CELL_CODE = 3;
    public static final int EXPLORE_CELL_CODE = 4;
    public static final int EXPLORE_HEAD_CELL_CODE = 5;
    public static final int FINAL_PATH_CELL_CODE = 6;

    public PathFinder() {
        super();

        board[1][1] = START_CELL_CODE; //for Start
        board[8][8] = OBSTACLE_CELL_CODE; //for obstacle
        board[15][15] = END_CELL_CODE; //for End
    }

    public final void resetGrid() {
        int i = 1;

        for(byte var2 = 15; i <= var2; ++i) {
            int j = 1;

            for(byte var4 = 15; j <= var4; ++j) {
                this.board[i][j] = 0;
            }
        }

        this.startX = 1;
        this.startY = 1;
        this.endX = 15;
        this.endY = 15;
        this.obstacleX = 8;
        this.obstacleY = 8;
        this.board[1][1] = 1;
        this.board[8][8] = 2;
        this.board[15][15] = 3;
    }

    public boolean solveBFS(long speed) {
        clearCells();
        BFS bfs = new BFS(rows, cols);
        bfs.solve(board, startX, startY, endX, endY, speed);
        return drawSolution(bfs.getPath());
    }

    public boolean solveDFS(long speed) {
        clearCells();

        DFS dfs = new DFS(rows, cols);
        dfs.solve(speed, startX, startY, endX, endY, board);
        return drawSolution(dfs.getPath());

    }

    /*
     *this function traces back the path
     */
    private boolean drawSolution(char[][] path) {
        int i = endX;
        int j = endY;

        if (path[j][i] == 'O') return false;
        while (path[j][i] != 'O') {
            //delay(20);
            switch (path[j][i]) {
                case 'U': j--;
                    break;
                case 'D': j++;
                    break;
                case 'L': i--;
                    break;
                default:
                    i++;
            }
            board[j][i] = FINAL_PATH_CELL_CODE;
        }
        return true;
    }

    /*
     * Clears the cell to it's default state
     * */
    private void clearCells() {
        for (int i=1; i <= 15; ++i) {
            for (int j=1; j <= 15; ++j) {
                if (this.board[i][j] == EXPLORE_CELL_CODE ||
                    this.board[i][j] == EXPLORE_HEAD_CELL_CODE ||
                    this.board[i][j] == FINAL_PATH_CELL_CODE
                ) {
                    this.board[i][j] = EMPTY_CELL_CODE;
                }
            }
        }
    }



    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getEndX() {
        return endX;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public int getEndY() {
        return endY;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }

    public int getObstacleX() {
        return obstacleX;
    }

    public void setObstacleX(int obstacleX) {
        this.obstacleX = obstacleX;
    }

    public int getObstacleY() {
        return obstacleY;
    }

    public void setObstacleY(int obstacleY) {
        this.obstacleY = obstacleY;
    }

    public int[][] getBoard() {
        return board;
    }
}
