package sg.edu.ntu.scse.mdp13.map2d;

import static java.lang.Math.abs;
import static java.lang.Math.ceil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;

import androidx.annotation.Nullable;

import sg.edu.ntu.scse.mdp13.R;
import sg.edu.ntu.scse.mdp13.R.styleable;
import sg.edu.ntu.scse.mdp13.pathFinder.PathFinder;
import static sg.edu.ntu.scse.mdp13.pathFinder.PathFinder.EMPTY_CELL_CODE;
import static sg.edu.ntu.scse.mdp13.pathFinder.PathFinder.END_CELL_CODE;
import static sg.edu.ntu.scse.mdp13.pathFinder.PathFinder.EXPLORE_CELL_CODE;
import static sg.edu.ntu.scse.mdp13.pathFinder.PathFinder.EXPLORE_HEAD_CELL_CODE;
import static sg.edu.ntu.scse.mdp13.pathFinder.PathFinder.FINAL_PATH_CELL_CODE;
import static sg.edu.ntu.scse.mdp13.pathFinder.PathFinder.START_CELL_CODE;

public final class PathGrid extends View {
    private int pathColor;
    private int startColor;
    private int endColor;
    private int finalPathColor;
    private int exploreHeadColor;
    private int exploreColor;
    private Paint exploreHeadPaintColor = new Paint();
    private Paint pathPaintColor = new Paint();
    private Paint startPaintColor = new Paint();
    private Paint finalPathPaintColor = new Paint();
    private Paint endPaintColor = new Paint();
    private Paint explorePaintColor = new Paint();

    private int cellSize = 0;
    private PathFinder _finder = new PathFinder();
    private int turn = 0;
    private boolean isSolving = false;

    public static final int START_BLOCK_TURN = -1;
    public static final int END_BLOCK_TURN = 1;

    public PathGrid(Context context) {
        super(context);
    }

    public PathGrid(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray =
                context.getTheme().obtainStyledAttributes(attrs, styleable.PathGrid, 0, 0);
        try {
            this.pathColor = typedArray.getInteger(R.styleable.PathGrid_gridColor, 0);
            this.startColor = typedArray.getInteger(R.styleable.PathGrid_startColor, 0);
            this.endColor = typedArray.getInteger(R.styleable.PathGrid_endColor, 0);
            this.exploreColor = typedArray.getInteger(R.styleable.PathGrid_exploreColor, 0);
            this.exploreHeadColor = typedArray.getInteger(R.styleable.PathGrid_exploreHeadColor, 0);
            this.finalPathColor = typedArray.getInteger(R.styleable.PathGrid_finalPathColor, 0);
        } finally {
            typedArray.recycle();
        }
    }


    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int dimension = Math.min(height, width);
        this.cellSize = dimension / 15;
        this.setMeasuredDimension(dimension, dimension);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.setPaint(this.pathPaintColor, this.pathColor);
        this.drawGrid(canvas);

        for(int i = 1; i <= 15; ++i) {
            for(int j = 1; j <= 15; ++j) {
                if (this._finder.getBoard()[i][j] == EXPLORE_CELL_CODE) {
                    this.setPaint(this.explorePaintColor, this.exploreColor);
                    this.colorCell(canvas, i, j, 12.0F, this.explorePaintColor);
                }

                // EXPLORE ANIMATION HEAD
                if (this._finder.getBoard()[i][j] == EXPLORE_HEAD_CELL_CODE) {
                    this.setPaint(this.exploreHeadPaintColor, this.exploreHeadColor);
                    this.colorCell(canvas, i, j, 0.0F, this.exploreHeadPaintColor);
                }

                if (this._finder.getBoard()[i][j] == FINAL_PATH_CELL_CODE) {
                    this.setPaint(this.finalPathPaintColor, this.finalPathColor);
                    this.colorCell(canvas, i, j, 12.0F, this.finalPathPaintColor);
                }
            }
        }

        this.setPaint(this.startPaintColor, this.startColor);
        this.colorCell(canvas, this._finder.getStartY(), this._finder.getStartX(), 5.0F, this.startPaintColor);
        this.colorCell(canvas, this._finder.getStartY(), this._finder.getStartX()+1, 5.0F, this.startPaintColor);
        this.colorCell(canvas, this._finder.getStartY()+1, this._finder.getStartX(), 5.0F, this.startPaintColor);
        this.colorCell(canvas, this._finder.getStartY()+1, this._finder.getStartX()+1, 5.0F, this.startPaintColor);

        this.setPaint(this.endPaintColor, this.endColor);
        this.colorCell(canvas, this._finder.getEndY(), this._finder.getEndX(), 5.0F, this.endPaintColor);
    }

    private final void colorCell(Canvas canvas, int r, int c, float radius, Paint paintColor) {
        RectF rectF = new RectF(
                (float)((c - 1) * this.cellSize),
                (float)((r - 1) * this.cellSize),
                (float)(c * this.cellSize),
                (float)(r * this.cellSize)
        );

        canvas.drawRoundRect(
                rectF, // rect
                radius, // rx
                radius, // ry
                paintColor // Paint
        );
        this.invalidate();
    }

    private final void setPaint(Paint paintColor, int color) {
        paintColor.setStyle(Style.FILL);
        paintColor.setColor(color);
        paintColor.setAntiAlias(true);
    }

    public final PathFinder getFinder() {
        return this._finder;
    }

    private final void drawGrid(Canvas canvas) {
        for(int i = 1; i <= 15; ++i) {
            for(int j = 1; j <= 15; ++j) {
                RectF rectF = new RectF(
                        (float)((j - 1) * this.cellSize) + (float)1,
                        (float)((i - 1) * this.cellSize) + (float)1,
                        (float)(j * this.cellSize) - (float)1,
                        (float)(i * this.cellSize) - (float)1
                );
                int cornersRadius = 5;
                canvas.drawRoundRect(
                        rectF, // rect
                        (float)cornersRadius, // rx
                        (float)cornersRadius, // ry
                        this.pathPaintColor // Paint
                );
            }
        }
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        if (!this.isSolving) {
            int y;
            int x;
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    y = (int) (ceil(eventY / cellSize));
                    x = (int) (ceil(eventX / cellSize));

                    this.turn = ((x == this._finder.getStartX() && y == this._finder.getStartY()) ||
                                    (x == this._finder.getStartX() && y == this._finder.getStartY()+1) ||
                                    (x == this._finder.getStartX()+1 && y == this._finder.getStartY()) ||
                                    (x == this._finder.getStartX()+1 && y == this._finder.getStartY()+1))
                                    ? START_BLOCK_TURN
                                    : END_BLOCK_TURN;
                    break;
                // MOVE CELL 1 by 1
                case MotionEvent.ACTION_MOVE:
                    y = (int) (ceil(eventY / cellSize));
                    x = (int) (ceil(eventX / cellSize));
                    this.moveCell(x, y, turn, 0);
                    break;
            }

            this.invalidate();
        }

        return true;
    }

    private void moveCell(int x, int y, int turn, int firstTouch) throws ArrayIndexOutOfBoundsException {
        // MOVE BLUE START/ROBOT BLOCK
        if (turn == START_BLOCK_TURN) {
            if (x >= 1 && y >= 1 && x+1 <= 15 && y+1 <= 15 &&
                    (this._finder.getBoard()[y][x] == EMPTY_CELL_CODE)
            ) {

                this._finder.getBoard()[this._finder.getStartY()][this._finder.getStartX()] = EMPTY_CELL_CODE;
                this._finder.getBoard()[this._finder.getStartY()][this._finder.getStartX()+1] = EMPTY_CELL_CODE;
                this._finder.getBoard()[this._finder.getStartY()+1][this._finder.getStartX()] = EMPTY_CELL_CODE;
                this._finder.getBoard()[this._finder.getStartY()+1][this._finder.getStartX()+1] = EMPTY_CELL_CODE;

                this._finder.setStartX(x);
                this._finder.setStartY(y);

                this._finder.getBoard()[this._finder.getStartY()][this._finder.getStartX()] = START_CELL_CODE;
                this._finder.getBoard()[this._finder.getStartY()][this._finder.getStartX()+1] = START_CELL_CODE;
                this._finder.getBoard()[this._finder.getStartY()+1][this._finder.getStartX()] = START_CELL_CODE;
                this._finder.getBoard()[this._finder.getStartY()+1][this._finder.getStartX()+1] = START_CELL_CODE;
            }
        // MOVE RED END/TARGET BLOCK
        } else if (turn == END_BLOCK_TURN) {
            if (x >= 1 && y >= 1 && x <= 15 && y <= 15 &&
                    (this._finder.getBoard()[y][x] == EMPTY_CELL_CODE)
            ) {
                this._finder.getBoard()[this._finder.getEndY()][this._finder.getEndX()] = EMPTY_CELL_CODE;
                this._finder.setEndX(x);
                this._finder.setEndY(y);
                this._finder.getBoard()[this._finder.getEndY()][this._finder.getEndX()] = END_CELL_CODE;
            }
        }
    }

    public final void setSolving(boolean flag) {
        this.isSolving = flag;
    }

}

