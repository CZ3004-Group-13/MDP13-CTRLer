package sg.edu.ntu.scse.mdp13.map;

public class GridMap {
    int startX = 1;
    int startY = 19;
    int endX = 10;
    int endY = 10;

    private int rows = 21;
    private int cols = 21;

    int[][] board = new int[rows][cols];

    public static final int EMPTY_CELL_CODE = 0;
    public static final int START_CELL_CODE = 1;
    public static final int END_CELL_CODE = 3;
    public static final int EXPLORE_CELL_CODE = 4;
    public static final int EXPLORE_HEAD_CELL_CODE = 5;
    public static final int FINAL_PATH_CELL_CODE = 6;

    public GridMap() {
        super();

        board[startX][startY] = START_CELL_CODE; //for Start
        board[10][10] = END_CELL_CODE; //for End
    }

    public final void resetGrid() {
        for(int i = 1; i <= 19; ++i)
            for(int j = 1; j <= 19; ++j)
                this.board[i][j] = 0;

        this.startX = 1;
        this.startY = 19;
        this.endX = 10;
        this.endY = 10;
        this.board[startX][startY] = START_CELL_CODE;
        this.board[10][10] = END_CELL_CODE;
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

    public int[][] getBoard() {
        return board;
    }
}
