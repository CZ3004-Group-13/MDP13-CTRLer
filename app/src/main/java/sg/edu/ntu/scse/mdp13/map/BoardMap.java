package sg.edu.ntu.scse.mdp13.map;

import static sg.edu.ntu.scse.mdp13.map.Target.TARGET_FACE_NORTH;

import java.util.Arrays;

public class BoardMap {
    int roboX = 1;
    int roboY = 19;
    Target[] targets = new Target[10];

    private int rows = 21;
    private int cols = 21;

    int[][] board = new int[rows][cols];

    public static final int EMPTY_CELL_CODE = 0;
    public static final int CAR_CELL_CODE = 1;
    public static final int TARGET_CELL_CODE = 3;
    public static final int EXPLORE_CELL_CODE = 4;
    public static final int EXPLORE_HEAD_CELL_CODE = 5;
    public static final int FINAL_PATH_CELL_CODE = 6;

    public BoardMap() {
        super();
        board[roboX][roboY] = CAR_CELL_CODE;

        targets[0] = new Target(10, 21-10); // 10, 10
        targets[1] = new Target(15, 21-15); // 15, 15
        targets[2] = new Target(15, 21-5); // 15, 5
        targets[3] = new Target(20, 21-20); // 20, 20
        targets[4] = new Target(20, 21-1); // 20, 1

        int n = 0;
        while (getTargets()[n] != null) {
            board[targets[n].getX()][targets[n].getY()] = TARGET_CELL_CODE;
            n++;
        }
    }

    public final void resetGrid() {
        for(int i = 1; i <= 19; ++i)
            for(int j = 1; j <= 19; ++j)
                this.board[i][j] = 0;

        this.roboX = 1;
        this.roboY = 19;
        Arrays.fill(targets, null);
        this.board[roboX][roboY] = CAR_CELL_CODE;

        targets[0] = new Target(10, 21-10); // 10, 10
        targets[1] = new Target(15, 21-15); // 15, 15
        targets[2] = new Target(15, 21-5); // 15, 5
        targets[3] = new Target(20, 21-20); // 20, 20
        targets[4] = new Target(20, 21-1); // 20, 1

        int n = 0;
        while (getTargets()[n] != null) {
            board[targets[n].getX()][targets[n].getY()] = TARGET_CELL_CODE;
            n++;
        }
    }


    public int getStartX() {
        return roboX;
    }

    public void setStartX(int roboX) {
        this.roboX = roboX;
    }

    public int getStartY() {
        return roboY;
    }

    public void setStartY(int roboY) {
        this.roboY = roboY;
    }

    public Target[] getTargets() {
        return targets;
    }

    public Target findTarget(int x, int y) {
        int n = 0;
        while (getTargets()[n] != null) {
            if (targets[n].getX() == x && targets[n].getY() == y)
                return targets[n];

            n++;
        }
        return null;
    }

    public void setTargets(Target[] targets) {
        this.targets = targets;
    }

    public int[][] getBoard() {
        return board;
    }
}
