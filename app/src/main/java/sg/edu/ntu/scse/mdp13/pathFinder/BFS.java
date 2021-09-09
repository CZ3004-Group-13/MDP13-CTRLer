package sg.edu.ntu.scse.mdp13.pathFinder;

import java.util.*;

import android.os.Handler;
import android.os.Looper;
import android.util.*;

public class BFS {
    int rows, cols;
    private int[][] dist;
    private char[][] path;
    Handler h = new Handler(Looper.getMainLooper());

    void solve(
            int[][] board,
            int startX,
            int startY,
            int endX,
            int endY,
            long speed
    ) {


        for (int i=1; i <= 15; ++i) {
            for (int j=1; j <= 15; ++j) {
                dist[i][j] = 999;
            }
        }
        for (int i=1; i <= 15; ++i) {
            for (int j=1; j <= 15; ++j) {
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


        Queue<Pair<Integer, Integer>> queue = new LinkedList<>();

        queue.offer(new Pair<>(startX, startY));
        dist[startX][startY] = 0;

        while (!queue.isEmpty()) {
            int currX = queue.element().first;
            int currY = queue.element().second;
            queue.poll();

            final boolean[] flag = {true};

            for (int i=0; i <= 3; ++i) {
                int iCopy = i;
                if (isValid(currX + dx.get(i), currY + dy.get(i), path, board)) {
                    if (1 + dist[currX][currY] < dist[currX + dx.get(i)][currY + dy.get(i)]) {

                        board[currY + dy.get(i)][currX + dx.get(i)] = PathFinder.EXPLORE_HEAD_CELL_CODE;

                        h.postDelayed(new Runnable() { //delay(speed)
                            @Override
                            public void run() {
                                switch (iCopy) {
                                    case 0:
                                        path[currY + dy.get(iCopy)][currX + dx.get(iCopy)] = 'U';
                                        break;
                                    case 1:
                                        path[currY + dy.get(iCopy)][currX + dx.get(iCopy)] = 'L';
                                        break;
                                    case 2:
                                        path[currY + dy.get(iCopy)][currX + dx.get(iCopy)] = 'D';
                                        break;
                                    default:
                                        path[currY + dy.get(iCopy)][currX + dx.get(iCopy)] = 'R';
                                }
                                if ((currX + dx.get(iCopy) == endX) && (currY + dy.get(iCopy) == endY)) {
                                    board[currY + dy.get(iCopy)][currX + dx.get(iCopy)] = PathFinder.EXPLORE_CELL_CODE;
                                    flag[0] = false;
                                    return; //break;
                                }
                                dist[currX + dx.get(iCopy)][currY + dy.get(iCopy)] = dist[currX][currY] + 1;
                                queue.offer(new Pair<>(currX + dx.get(iCopy), currY + dy.get(iCopy)));
                                board[currY + dy.get(iCopy)][currX + dx.get(iCopy)] = PathFinder.EXPLORE_CELL_CODE;
                            }
                        },speed);

                    }
                }
            }
            if (!flag[0]) {
                break;
            }
        }
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

    public BFS(int rows, int cols) {
        super();
        this.rows = rows;
        this.cols = cols;
        dist = new int[rows][cols];
        path = new char[rows][cols];
    }
}
