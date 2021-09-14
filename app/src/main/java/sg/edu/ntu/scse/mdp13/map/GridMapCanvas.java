package sg.edu.ntu.scse.mdp13.map;

import static java.lang.Math.abs;
import static java.lang.Math.ceil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import sg.edu.ntu.scse.mdp13.R;
import sg.edu.ntu.scse.mdp13.R.styleable;

import static sg.edu.ntu.scse.mdp13.map.GridMap.EMPTY_CELL_CODE;
import static sg.edu.ntu.scse.mdp13.map.GridMap.TARGET_CELL_CODE;
import static sg.edu.ntu.scse.mdp13.map.GridMap.EXPLORE_CELL_CODE;
import static sg.edu.ntu.scse.mdp13.map.GridMap.EXPLORE_HEAD_CELL_CODE;
import static sg.edu.ntu.scse.mdp13.map.GridMap.FINAL_PATH_CELL_CODE;
import static sg.edu.ntu.scse.mdp13.map.GridMap.ROBO_CELL_CODE;

public final class GridMapCanvas extends View {
    private int pathColor;
    private int startColor;
    private int endColor;
    private int finalPathColor;
    private int exploreHeadColor;
    private int exploreColor;
    private int wheelColor;
    private int tarNumColor;
    private Paint exploreHeadPaintColor = new Paint();
    private Paint pathPaintColor = new Paint();
    private Paint startPaintColor = new Paint();
    private Paint finalPathPaintColor = new Paint();
    private Paint endPaintColor = new Paint();
    private Paint explorePaintColor = new Paint();
    private Paint wheelPaintColor = new Paint();
    private Paint tarNumPaintColor = new Paint();

    private int cellSize = 0;
    private GridMap _map = new GridMap();
    private int turn = 0;
    private boolean isSolving = false;

    public static final int ROBO_BLOCK_TURN = -1;
    public static final int TARGET_BLOCK_TURN = 1;

    public GridMapCanvas(Context context) {
        super(context);
    }

    public GridMapCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray =
                context.getTheme().obtainStyledAttributes(attrs, styleable.GridMap, 0, 0);
        try {
            this.pathColor = typedArray.getInteger(R.styleable.GridMap_gridColor, 0);
            this.startColor = typedArray.getInteger(R.styleable.GridMap_roboColor, 0);
            this.endColor = typedArray.getInteger(R.styleable.GridMap_targetColor, 0);
            this.exploreColor = typedArray.getInteger(R.styleable.GridMap_exploreColor, 0);
            this.exploreHeadColor = typedArray.getInteger(R.styleable.GridMap_exploreHeadColor, 0);
            this.finalPathColor = typedArray.getInteger(R.styleable.GridMap_finalPathColor, 0);
            this.wheelColor = typedArray.getInteger(R.styleable.GridMap_wheelColor, 0);
            this.tarNumColor = typedArray.getInteger(styleable.GridMap_tarNumColor, 0);
        } finally {
            typedArray.recycle();
        }
    }


    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int dimension = Math.min(height, width);
        this.cellSize = dimension / 20;
        this.setMeasuredDimension(dimension, dimension);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.setPaint(this.pathPaintColor, this.pathColor);
        this.drawGrid(canvas);
        this.drawGridNumber(canvas);

        for(int i = 1; i < 20; ++i) {
            for(int j = 1; j < 20; ++j) {
                if (this._map.getBoard()[i][j] == EXPLORE_CELL_CODE) {
                    this.setPaint(this.explorePaintColor, this.exploreColor);
                    this.colorCell(canvas, i, j, 12.0F, this.explorePaintColor);
                }

                // EXPLORE ANIMATION HEAD
                if (this._map.getBoard()[i][j] == EXPLORE_HEAD_CELL_CODE) {
                    this.setPaint(this.exploreHeadPaintColor, this.exploreHeadColor);
                    this.colorCell(canvas, i, j, 0.0F, this.exploreHeadPaintColor);
                }

                if (this._map.getBoard()[i][j] == FINAL_PATH_CELL_CODE) {
                    this.setPaint(this.finalPathPaintColor, this.finalPathColor);
                    this.colorCell(canvas, i, j, 12.0F, this.finalPathPaintColor);
                }
            }
        }

        this.drawCar(canvas);

        this.drawTarget(canvas);
    }

    private void colorCell(Canvas canvas, int r, int c, float radius, Paint paintColor) {
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

    private void setPaint(Paint paintColor, int color) {
        paintColor.setStyle(Style.FILL);
        paintColor.setColor(color);
        paintColor.setAntiAlias(true);
    }

    public final GridMap getFinder() {
        return this._map;
    }

    private void drawGrid(Canvas canvas) {
        for(int i = 0; i <= 20; ++i) {
            for(int j = 0; j <= 20; ++j) {
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

    private void drawGridNumber(Canvas canvas) {
        float halfSize = this.cellSize * 0.38f;
        for (int x = 19; x >= 0; --x) {
            // Left Vertical
            canvas.drawText(Integer.toString(x), this.cellSize * x + halfSize, this.cellSize * 19.6f, endPaintColor);
            // Bottom Horizontal
            canvas.drawText(Integer.toString(19-x), halfSize, this.cellSize * x + halfSize * 1.5f, endPaintColor);
        }
    }

    private void drawCar(Canvas canvas) {
        this.setPaint(this.startPaintColor, this.startColor);

        this.colorCell(canvas, this._map.getStartY(), this._map.getStartX(), 5.0F, this.startPaintColor);
        this.colorCell(canvas, this._map.getStartY(), this._map.getStartX()+1, 5.0F, this.startPaintColor);
        this.colorCell(canvas, this._map.getStartY()+1, this._map.getStartX(), 5.0F, this.startPaintColor);
        this.colorCell(canvas, this._map.getStartY()+1, this._map.getStartX()+1, 5.0F, this.startPaintColor);

        this.setPaint(this.wheelPaintColor, this.wheelColor);


        // Draw wheels
        RectF rectF = new RectF(
                (float)((this._map.getStartX() - 0.75) * this.cellSize), //left
                (float)((this._map.getStartY() - 0.65) * this.cellSize), //top - bound
                (float)((this._map.getStartX() - 0.25) * this.cellSize), //right - width
                (float)((this._map.getStartY() + 0.15) * this.cellSize) //bottom - height
        );

        canvas.drawRoundRect(
                rectF, // rect
                5F, // rx
                5F, // ry
                this.wheelPaintColor // Paint
        );

        RectF rectv = new RectF(
                (float)((this._map.getStartX() - 0.75 + 1) * this.cellSize), //left
                (float)((this._map.getStartY() - 0.65) * this.cellSize), //top - bound
                (float)((this._map.getStartX() - 0.25 + 1) * this.cellSize), //right - width
                (float)((this._map.getStartY() + 0.15) * this.cellSize) //bottom - height
        );

        canvas.drawRoundRect(
                rectv, // rect
                5F, // rx
                5F, // ry
                this.wheelPaintColor // Paint
        );

        this.invalidate();
    }

    private void drawTarget(Canvas canvas) {
        int x = this._map.getEndX();
        int y = this._map.getEndY();
        int halfWidth = this.cellSize / 2;
        this.setPaint(this.endPaintColor, this.endColor);
        this.colorCell(canvas, y, x, 5.0F, this.endPaintColor);

        this.setPaint(this.tarNumPaintColor, this.tarNumColor);

        canvas.drawText(Integer.toString(1),
                this.cellSize * (x-1) + this.cellSize * 0.4f,
                this.cellSize * y - this.cellSize*0.4f,
                tarNumPaintColor
        );

        this.setPaint(this.startPaintColor, this.startColor);

        RectF dirFace = new RectF(
                (float)((this._map.getEndX() - 1) * this.cellSize), //left
                (float)((this._map.getEndY() - 0.9) * this.cellSize), //top - bound
                (float)(this._map.getEndX() * this.cellSize), //right - width
                (float)((this._map.getEndY() - 1) * this.cellSize) //bottom - height
        );

        canvas.drawRoundRect(
                dirFace, // rect
                5F, // rx
                5F, // ry
                this.startPaintColor // Paint
        );

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

                    this.turn = (x == this._map.getEndX() && y == this._map.getEndY())
                                    ? TARGET_BLOCK_TURN
                                    : ROBO_BLOCK_TURN;
                    break;
                // MOVE CELL 1 by 1
                case MotionEvent.ACTION_MOVE:
                    y = (int) (ceil(eventY / cellSize));
                    x = (int) (ceil(eventX / cellSize));
                    this.dragCell(x, y, turn, 0);
                    break;
            }

            this.invalidate();
        }

        return true;
    }

    private void dragCell(int x, int y, int turn, int firstTouch) throws ArrayIndexOutOfBoundsException {
        // MOVE BLUE ROBO/ROBOT BLOCK
        boolean isInGrid = x >= 1 && y >= 1 && x+1 <= 20 && y+1 <= 20;
        if (turn == ROBO_BLOCK_TURN) {
            if (isInGrid && (this._map.getBoard()[x][y] == EMPTY_CELL_CODE)
                && (this._map.getBoard()[x][y] != TARGET_CELL_CODE) && (this._map.getBoard()[x+1][y+1] != TARGET_CELL_CODE)
                && (this._map.getBoard()[x][y+1] != TARGET_CELL_CODE) && (this._map.getBoard()[x+1][y] != TARGET_CELL_CODE)
            ) {

                this._map.getBoard()[this._map.getStartX()][this._map.getStartY()] = EMPTY_CELL_CODE;
                this._map.getBoard()[this._map.getStartX()][this._map.getStartY()+1] = EMPTY_CELL_CODE;
                this._map.getBoard()[this._map.getStartX()+1][this._map.getStartY()] = EMPTY_CELL_CODE;
                this._map.getBoard()[this._map.getStartX()+1][this._map.getStartY()+1] = EMPTY_CELL_CODE;

                this._map.setStartX(x);
                this._map.setStartY(y);

                this._map.getBoard()[this._map.getStartX()][this._map.getStartY()] = ROBO_CELL_CODE;
                this._map.getBoard()[this._map.getStartX()][this._map.getStartY()+1] = ROBO_CELL_CODE;
                this._map.getBoard()[this._map.getStartX()+1][this._map.getStartY()] = ROBO_CELL_CODE;
                this._map.getBoard()[this._map.getStartX()+1][this._map.getStartY()+1] = ROBO_CELL_CODE;
            }
        // MOVE RED TARGET/TARGET BLOCK
        } else if (turn == TARGET_BLOCK_TURN) {
            if (isInGrid && (this._map.getBoard()[x][y] == EMPTY_CELL_CODE)
                && (this._map.getBoard()[x][y] != ROBO_CELL_CODE)
            ) {
                this._map.getBoard()[this._map.getEndX()][this._map.getEndY()] = EMPTY_CELL_CODE;
                this._map.setEndX(x);
                this._map.setEndY(y);
                this._map.getBoard()[this._map.getEndX()][this._map.getEndY()] = TARGET_CELL_CODE;
            }
        }
    }

    public final void setSolving(boolean flag) {
        this.isSolving = flag;
    }
}

