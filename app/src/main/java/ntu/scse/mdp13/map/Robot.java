package ntu.scse.mdp13.map;

public class Robot {
    private int x, y, s, m, f;

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

    public Robot() {
        this.x = 1;
        this.y = 19;
        this.s = ROBOT_SERVO_CENTRE;
        this.m = ROBOT_MOTOR_STOP;
        this.f = ROBOT_FACE_NORTH;
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
        boolean isCarInGrid = x >= 1 && x+1 <= 20;
        if (f == ROBOT_FACE_NORTH && isCarInGrid) {
            boolean forwardInGrid = y-1 >= 1;
            boolean reverseInGrid = y+1 < 20;

            if ((direction == ROBOT_MOTOR_FORWARD && forwardInGrid) || (direction == ROBOT_MOTOR_REVERSE && reverseInGrid)) {
                this.m = direction == ROBOT_MOTOR_FORWARD ? ROBOT_MOTOR_FORWARD : ROBOT_MOTOR_REVERSE;
                this.y += direction == ROBOT_MOTOR_FORWARD ? -1 : 1;
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
