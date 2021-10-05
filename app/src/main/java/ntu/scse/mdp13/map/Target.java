package ntu.scse.mdp13.map;

public class Target {
    private int x, y, n, f;
    private int img = 0;

    public static final int TARGET_FACE_NORTH = 0;
    public static final int TARGET_FACE_EAST = 1;
    public static final int TARGET_FACE_SOUTH = 2;
    public static final int TARGET_FACE_WEST = 3;

    public Target(int x, int y, int n) {
        this.x = x;
        this.y = y;
        this.n = n;
        this.f = 0;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public int getImg() {
        return img;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getF() {
        return f;
    }

    public void cycleFaceClockwise(boolean clockwise) {
        if (clockwise)
            this.f = this.f == TARGET_FACE_WEST ? TARGET_FACE_NORTH : this.f + 1;
        else
            this.f = this.f == TARGET_FACE_NORTH ? TARGET_FACE_WEST : this.f - 1;
    }

    @Override
    public String toString() {
        return "Target{" +
                "n=" + n +
                '}';
    }
}
