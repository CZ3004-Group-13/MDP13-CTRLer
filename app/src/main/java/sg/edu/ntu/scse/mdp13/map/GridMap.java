package sg.edu.ntu.scse.mdp13.map;

public class GridMap {
    int roboX = 1;
    int roboY = 19;
    int targetX = 10;
    int targetY = 10;

    private int rows = 21;
    private int cols = 21;

    int[][] board = new int[rows][cols];

    public static final int EMPTY_CELL_CODE = 0;
    public static final int ROBO_CELL_CODE = 1;
    public static final int TARGET_CELL_CODE = 3;
    public static final int EXPLORE_CELL_CODE = 4;
    public static final int EXPLORE_HEAD_CELL_CODE = 5;
    public static final int FINAL_PATH_CELL_CODE = 6;

    public GridMap() {
        super();

        board[roboX][roboY] = ROBO_CELL_CODE; //for Start
        board[10][10] = TARGET_CELL_CODE; //for End
    }

    public final void resetGrid() {
        for(int i = 1; i <= 19; ++i)
            for(int j = 1; j <= 19; ++j)
                this.board[i][j] = 0;

        this.roboX = 1;
        this.roboY = 19;
        this.targetX = 10;
        this.targetY = 10;
        this.board[roboX][roboY] = ROBO_CELL_CODE;
        this.board[10][10] = TARGET_CELL_CODE;
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

    public int getEndX() {
        return targetX;
    }

    public void setEndX(int targetX) {
        this.targetX = targetX;
    }

    public int getEndY() {
        return targetY;
    }

    public void setEndY(int targetY) {
        this.targetY = targetY;
    }

    public int[][] getBoard() {
        return board;
    }
}
