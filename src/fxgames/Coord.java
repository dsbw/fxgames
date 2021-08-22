package fxgames;

public class Coord {
    public int x;
    public int y;

    public Coord(int aX, int aY) {
        x = aX;
        y = aY;
    }

    @Override
    public String toString() {
        return "Coord{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}