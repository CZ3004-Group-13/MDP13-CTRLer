package ntu.scse.mdp13.map;

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
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;



import ntu.scse.mdp13.R;
import ntu.scse.mdp13.R.styleable;

import static ntu.scse.mdp13.map.BoardMap.EMPTY_CELL_CODE;
import static ntu.scse.mdp13.map.BoardMap.TARGET_CELL_CODE;
import static ntu.scse.mdp13.map.BoardMap.EXPLORE_CELL_CODE;
import static ntu.scse.mdp13.map.BoardMap.EXPLORE_HEAD_CELL_CODE;
import static ntu.scse.mdp13.map.BoardMap.FINAL_PATH_CELL_CODE;
import static ntu.scse.mdp13.map.BoardMap.CAR_CELL_CODE;
import static ntu.scse.mdp13.map.Robot.ROBOT_SERVO_CENTRE;
import static ntu.scse.mdp13.map.Robot.ROBOT_SERVO_LEFT;
import static ntu.scse.mdp13.map.Robot.ROBOT_SERVO_RIGHT;
import static ntu.scse.mdp13.map.Target.TARGET_FACE_NORTH;
import static ntu.scse.mdp13.map.Target.TARGET_FACE_EAST;
import static ntu.scse.mdp13.map.Target.TARGET_FACE_WEST;
import static ntu.scse.mdp13.map.Target.TARGET_FACE_SOUTH;

public final class MapCanvas extends View {
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
    private BoardMap _map = new BoardMap();
    private int turn = 0;
    private boolean isSolving = false;

    public static final int NEW_TARGET_TURN = 0;
    public static final int CAR_BLOCK_TURN = -1;
    public static final int TARGET_BLOCK_TURN = 1;

    public MapCanvas(Context context) {
        super(context);
    }

    public MapCanvas(Context context, @Nullable AttributeSet attrs) {
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

        int n = 0;
        while (n < _map.getTargets().size()) {
            this.drawTarget(canvas, n);
            n++;
        }

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

    public final BoardMap getFinder() {
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
            canvas.drawText(Integer.toString(x+1), this.cellSize * x + halfSize, this.cellSize * 19.6f, endPaintColor);
            // Bottom Horizontal
            canvas.drawText(Integer.toString(20-x), halfSize, this.cellSize * x + halfSize * 1.5f, endPaintColor);
        }
    }

    private void drawCar(Canvas canvas) {
        this.setPaint(this.startPaintColor, this.startColor);

        this.colorCell(canvas, this._map.getRobo().getY(), this._map.getRobo().getX(), 5.0F, this.startPaintColor);
        this.colorCell(canvas, this._map.getRobo().getY(), this._map.getRobo().getX() + 1, 5.0F, this.startPaintColor);
        this.colorCell(canvas, this._map.getRobo().getY() + 1, this._map.getRobo().getX(), 5.0F, this.startPaintColor);
        this.colorCell(canvas, this._map.getRobo().getY() + 1, this._map.getRobo().getX() + 1, 5.0F, this.startPaintColor);

        this.setPaint(this.wheelPaintColor, this.wheelColor);

        // Draw servo
        if (this._map.getRobo().getServo() != ROBOT_SERVO_CENTRE) {
            canvas.save();

            switch(this._map.getRobo().getServo()) {
                case ROBOT_SERVO_LEFT:
                    canvas.rotate(-45, this._map.getRobo().getX() + 0.33f * this.cellSize, this._map.getRobo().getY() + 18.25f * this.cellSize);
                    break;
                case ROBOT_SERVO_RIGHT:
                    canvas.rotate(45, this._map.getRobo().getX() + 0.5f * this.cellSize, this._map.getRobo().getY() + 18.25f * this.cellSize);
                    break;
            }
        }

        RectF rectF = new RectF(
                (float)((this._map.getRobo().getX() - 0.75) * this.cellSize), //left
                (float)((this._map.getRobo().getY() - 0.65) * this.cellSize), //top - bound
                (float)((this._map.getRobo().getX() - 0.25) * this.cellSize), //right - width
                (float)((this._map.getRobo().getY() + 0.15) * this.cellSize) //bottom - height
        );

        canvas.drawRoundRect(
                rectF, // rect
                5F, // rx
                5F, // ry
                this.wheelPaintColor // Paint
        );

        if (this._map.getRobo().getServo() != ROBOT_SERVO_CENTRE) {
            canvas.restore();
            canvas.save();

            switch(this._map.getRobo().getServo()) {
                case ROBOT_SERVO_LEFT:
                    canvas.rotate(-45, this._map.getRobo().getX() + 1.33f * this.cellSize, this._map.getRobo().getY() + 18.25f * this.cellSize);
                    break;
                case ROBOT_SERVO_RIGHT:
                    canvas.rotate(45, this._map.getRobo().getX() + 1.5f * this.cellSize, this._map.getRobo().getY() + 18.25f * this.cellSize);
                    break;
            }
        }

        RectF rectv = new RectF(
                (float)((this._map.getRobo().getX() - 0.75 + 1) * this.cellSize), //left
                (float)((this._map.getRobo().getY() - 0.65) * this.cellSize), //top - bound
                (float)((this._map.getRobo().getX() - 0.25 + 1) * this.cellSize), //right - width
                (float)((this._map.getRobo().getY() + 0.15) * this.cellSize) //bottom - height
        );

        canvas.drawRoundRect(
                rectv, // rect
                5F, // rx
                5F, // ry
                this.wheelPaintColor // Paint
        );


        if (this._map.getRobo().getServo() == ROBOT_SERVO_LEFT || this._map.getRobo().getServo() == ROBOT_SERVO_RIGHT) canvas.restore();

        this.invalidate();
    }

    private void drawTarget(Canvas canvas, int tarNum) {
        int x = this._map.getTargets().get(tarNum).getX();
        int y = this._map.getTargets().get(tarNum).getY();

        this.setPaint(this.endPaintColor, this.endColor);
        this.colorCell(canvas, y, x, 5.0F, this.endPaintColor);

        this.setPaint(this.tarNumPaintColor, this.tarNumColor);

        canvas.drawText(Integer.toString(tarNum+1),
                this.cellSize * (x-1) + this.cellSize * 0.4f,
                this.cellSize * y - this.cellSize*0.4f,
                tarNumPaintColor
        );

        this.setPaint(this.startPaintColor, this.startColor);

        float leftBound = 0, topBound = 0, rightBound = 0, bottomBound = 0;
        switch(_map.getTargets().get(tarNum).getF()) {
            case TARGET_FACE_NORTH:
                leftBound = -1;
                topBound = -0.9f;
                rightBound = 0;
                bottomBound = -1;
                break;
            case TARGET_FACE_EAST:
                leftBound = -0.1f;
                topBound = -1;
                rightBound = 0;
                bottomBound = 0;
                break;
            case TARGET_FACE_SOUTH:
                leftBound = -1;
                topBound = -0.1f;
                rightBound = 0;
                bottomBound = 0;
                break;
            case TARGET_FACE_WEST:
                leftBound = -1;
                topBound = -1;
                rightBound = -0.9f;
                bottomBound = 0;
                break;
        }

        RectF fRect = new RectF(
                (float)((_map.getTargets().get(tarNum).getX() + leftBound) * this.cellSize), //left
                (float)((_map.getTargets().get(tarNum).getY() + topBound) * this.cellSize), //top
                (float)((_map.getTargets().get(tarNum).getX() + rightBound) * this.cellSize), //right
                (float)((_map.getTargets().get(tarNum).getY() + bottomBound) * this.cellSize) //bottom
        );
        canvas.drawRoundRect(
                fRect, // rect
                5F, // rx
                5F, // ry
                this.startPaintColor // Paint
        );
        this.invalidate();
    }
    
    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        if (!this.isSolving) {
            int y;
            int x;
            Target t;
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    y = (int) (ceil(eventY / cellSize));
                    x = (int) (ceil(eventX / cellSize));
                    t = _map.findTarget(x, y);

                    final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
                        public void onLongPress(MotionEvent e) {
                            dragCell(x, y, turn, 1);
                        }
                    });

                    this.turn = (t != null)
                            ? TARGET_BLOCK_TURN
                            : ((x == this._map.getRobo().getX() && y == this._map.getRobo().getY()) ||
                            (x == this._map.getRobo().getX() && y == this._map.getRobo().getY()+1) ||
                            (x == this._map.getRobo().getX()+1 && y == this._map.getRobo().getY()) ||
                            (x == this._map.getRobo().getX()+1 && y == this._map.getRobo().getY()+1))
                            ? CAR_BLOCK_TURN
                            : NEW_TARGET_TURN;

                    if (this.turn == TARGET_BLOCK_TURN)
                        t.cycleFaceClockwise(true);
                        _map.setLastTouchedTarget(t);

                    if (this.turn == NEW_TARGET_TURN) {
                        // LONG PRESS TO MAKE NEW
                        return gestureDetector.onTouchEvent(event);
                    }

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
        // MOVE BLUE START BLOCK
        boolean isTargetInGrid = x >= 1 && y >= 1 && x <= 20 && y <= 20;
        if (turn == CAR_BLOCK_TURN) {
            boolean isCarInGrid = x >= 1 && y >= 1 && x+1 <= 20 && y+1 <= 20;
            if (isCarInGrid && (this._map.getBoard()[x][y] == EMPTY_CELL_CODE)
                && (this._map.getBoard()[x][y] != TARGET_CELL_CODE) && (this._map.getBoard()[x+1][y+1] != TARGET_CELL_CODE)
                && (this._map.getBoard()[x][y+1] != TARGET_CELL_CODE) && (this._map.getBoard()[x+1][y] != TARGET_CELL_CODE)
            ) {

                this._map.getBoard()[this._map.getRobo().getX()][this._map.getRobo().getY()] = EMPTY_CELL_CODE;
                this._map.getBoard()[this._map.getRobo().getX()][this._map.getRobo().getY()+1] = EMPTY_CELL_CODE;
                this._map.getBoard()[this._map.getRobo().getX()+1][this._map.getRobo().getY()] = EMPTY_CELL_CODE;
                this._map.getBoard()[this._map.getRobo().getX()+1][this._map.getRobo().getY()+1] = EMPTY_CELL_CODE;

                this._map.getRobo().setX(x);
                this._map.getRobo().setY(y);

                this._map.getBoard()[this._map.getRobo().getX()][this._map.getRobo().getY()] = CAR_CELL_CODE;
                this._map.getBoard()[this._map.getRobo().getX()][this._map.getRobo().getY()+1] = CAR_CELL_CODE;
                this._map.getBoard()[this._map.getRobo().getX()+1][this._map.getRobo().getY()] = CAR_CELL_CODE;
                this._map.getBoard()[this._map.getRobo().getX()+1][this._map.getRobo().getY()+1] = CAR_CELL_CODE;
            }

        } else if (turn == TARGET_BLOCK_TURN) {
            // MOVE RED TARGET BLOCK TO EMPTY CELL BUT AVOID CAR CELL
            if (isTargetInGrid && (this._map.getBoard()[x][y] == EMPTY_CELL_CODE)
                    && (this._map.getBoard()[x][y] != CAR_CELL_CODE)
            ) {
                Target t = _map.getLastTouchedTarget();
                this._map.getBoard()[t.getX()][t.getY()] = EMPTY_CELL_CODE;
                t.setX(x);
                t.setY(y);
                this._map.getBoard()[t.getX()][t.getY()] = TARGET_CELL_CODE;

            } else if (!isTargetInGrid) {
                Target t = this._map.getLastTouchedTarget();
                if (_map.getTargets().contains(t))
                    _map.dequeueTarget(t);
            }
        } else if (turn == NEW_TARGET_TURN) {
            if (isTargetInGrid && (this._map.getBoard()[y][x] == EMPTY_CELL_CODE)) {

                Target t = new Target(x, y, _map.getTargets().size());
                this._map.getBoard()[t.getX()][t.getY()] = TARGET_CELL_CODE;
                _map.getTargets().add(t);
            }
        }
    }

    public final void setSolving(boolean flag) {
        this.isSolving = flag;
    }
}

