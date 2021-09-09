package sg.edu.ntu.scse.mdp13.pathFinder;

import java.util.*;
import android.util.*;

public class DFS {
    int rows, cols;
    private int[][] dist;
    private char[][] path;

    void solve(
            long speed,
            int startX,
            int startY,
            int endX,
            int endY,
            int[][] board
    ) {

        for (int i=1; i <= 15; i++) {
            for (int j=1; j <= 15; j++) {
                dist[i][j] = 999;
            }
        }
        for (int i=1; i <= 15; i++) {
            for (int j=1; j <= 15; j++) {
                path[i][j] = 'O';
            }
        }
        ArrayList<Integer> dx = new ArrayList<Integer>() {
            {
                add(0);
                add(1);
                add(0);
                add(-1);
            }
        };

        ArrayList<Integer> dy = new ArrayList<Integer>() {
            {
                add(1);
                add(0);
                add(-1);
                add(0);
            }
        };

        dist[startX][startY] = 0;

        dfs(startX, startY, endX, endY, dx, dy, speed, board);

    }


    private boolean dfs(
            int startX,
            int startY,
            int endX,
            int endY,
            ArrayList<Integer> dx,
            ArrayList<Integer> dy,
            long speed,
            int[][] board
    ) {

        for (int i=0; i <= 3; ++i) {

            if (isValid(startX + dx.get(i), startY + dy.get(i), path, board)) {
                if (1 + dist[startX][startY] < dist[startX + dx.get(i)][startY + dy.get(i)]) {
                    board[startY + dy.get(i)][startX + dx.get(i)] = PathFinder.EXPLORE_HEAD_CELL_CODE;
                    //delay(speed)
                    switch (i) {
                        case 0:
                            path[startY + dy.get(i)][startX + dx.get(i)] = 'U';
                            break;
                        case 1:
                            path[startY + dy.get(i)][startX + dx.get(i)] = 'L';
                            break;
                        case 2:
                            path[startY + dy.get(i)][startX + dx.get(i)] = 'D';
                            break;
                        default:
                            path[startY + dy.get(i)][startX + dx.get(i)] = 'R';
                    }
                    if ((startX + dx.get(i) == endX) && (startY + dy.get(i) == endY)) {
                        board[startY + dy.get(i)][startX + dx.get(i)] = PathFinder.EXPLORE_CELL_CODE;
                        return true;
                    }
                    dist[startX + dx.get(i)][startY + dy.get(i)] = dist[startX][startY] + 1;
                    board[startY + dy.get(i)][startX + dx.get(i)] = PathFinder.EXPLORE_CELL_CODE;
                    if (dfs(startX + dx.get(i), startY + dy.get(i), endX, endY, dx, dy, speed, board))
                        return true;
                }
            }
        }

        board[startY][startX] = PathFinder.END_CELL_CODE;
        //delay(15)
        board[startY][startX] = PathFinder.EXPLORE_CELL_CODE;
        return false;
    }

    private boolean isValid(
            int i,
            int j,
            char[][] path,
            int[][] board
    ) {
        if (i >= 1 && j >= 1 && i <= 15 && j <= 15 && path[j][i] == 'O' && board[j][i] != PathFinder.OBSTACLE_CELL_CODE) {
            return true;
        }
        return false;
    }

    public char[][] getPath() { return this.path; }
    
    public DFS(int rows, int cols) {
        super();
        this.rows = rows;
        this.cols = cols;
        dist = new int[rows][cols];
        path = new char[rows][cols];
    }
}
