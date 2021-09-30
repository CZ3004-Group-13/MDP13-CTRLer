package ntu.scse.mdp13.map;

import static ntu.scse.mdp13.map.BoardMap.TARGET_CELL_CODE;

import ntu.scse.mdp13.bluetooth.BluetoothService;
import ntu.scse.mdp13.bluetooth.MessageFragment;

public class Robot {
    private int x, y, s, m, f;

    private BoardMap _map;

    public static final int ROBOT_MOTOR_FORWARD = 1;
    public static final int ROBOT_MOTOR_STOP = 0;
    public static final int ROBOT_MOTOR_REVERSE = -1;

    public static final int ROBOT_SERVO_LEFT = -1;
    public static final int ROBOT_SERVO_CENTRE = 0;
    public static final int ROBOT_SERVO_RIGHT = 1;

    public static final int ROBOT_FACE_NORTH = 0;
    public static final int ROBOT_FACE_EAST = 1;
    public static final int ROBOT_FACE_SOUTH = 2;
    public static final int ROBOT_FACE_WEST = 3;

    public static final String STM_COMMAND_FORWARD = "f";
    public static final String STM_COMMAND_REVERSE = "r";
    public static final String STM_COMMAND_STOP = "s";

    public Robot(BoardMap map) {
        this.x = 1;
        this.y = 19;
        this.s = ROBOT_SERVO_CENTRE;
        this.m = ROBOT_MOTOR_STOP;
        this.f = ROBOT_FACE_NORTH;
        this._map = map;
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public int getServo() { return s; }
    public void setServo(int s) { this.s = s; }

    public int getMotor() { return m; }
    public void setMotor(int m) { this.m = m; }

    public int getFacing() { return f; }
    public void setFacing(int f) { this.f = f; }

    public void motorRotate(int direction) {
        boolean willCarInGrid = x >= 1 && x+1 <= 20;

        if (f == ROBOT_FACE_NORTH && willCarInGrid) {
            boolean forwardInGrid = y-1 >= 1;
            boolean reverseInGrid = y+1 < 20;
            boolean forwardAvoidTarget = (this._map.getBoard()[x][y - 1] != TARGET_CELL_CODE) && (this._map.getBoard()[x + 1][y - 1] != TARGET_CELL_CODE);
            boolean reverseAvoidTarget = false;

            if ((direction == ROBOT_MOTOR_FORWARD && forwardInGrid) || (direction == ROBOT_MOTOR_REVERSE && reverseInGrid )) {

                if (direction == ROBOT_MOTOR_REVERSE)
                    reverseAvoidTarget = (this._map.getBoard()[x][y + 2] != TARGET_CELL_CODE) && (this._map.getBoard()[x + 1][y + 2] != TARGET_CELL_CODE);


                if ((direction == ROBOT_MOTOR_FORWARD && forwardAvoidTarget) || (direction == ROBOT_MOTOR_REVERSE && reverseAvoidTarget )) {
                    this.m = direction == ROBOT_MOTOR_FORWARD ? ROBOT_MOTOR_FORWARD : ROBOT_MOTOR_REVERSE;
                    this.y += direction == ROBOT_MOTOR_FORWARD ? -1 : 1;
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Robot{" +
                "x=" + x +
                ", y=" + y +
                ", s=" + s +
                ", m=" + m +
                ", f=" + f +
                '}';
    }
}
