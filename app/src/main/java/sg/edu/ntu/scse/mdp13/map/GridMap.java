package sg.edu.ntu.scse.mdp13.map;

public class GridMap {
    int startX = 1;
    int startY = 14;
    int endX = 8;
    int endY = 8;

    private int rows = 16;
    private int cols = 16;

    int[][] board = new int[rows][cols];

    public static final int EMPTY_CELL_CODE = 0;
    public static final int START_CELL_CODE = 1;
    public static final int END_CELL_CODE = 3;
    public static final int EXPLORE_CELL_CODE = 4;
    public static final int EXPLORE_HEAD_CELL_CODE = 5;
    public static final int FINAL_PATH_CELL_CODE = 6;

    public GridMap() {
        super();

        board[1][14] = START_CELL_CODE; //for Start
        board[8][8] = END_CELL_CODE; //for End
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
        this.startY = 14;
        this.endX = 8;
        this.endY = 8;
        this.board[1][1] = 1;
        this.board[15][15] = 3;
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

    public int[][] getBoard() {
        return board;
    }
}
