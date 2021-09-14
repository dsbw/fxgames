package fxgames;

public class Coord {
    public int x;
    public int y;

    public Coord(int aX, int aY) {
        x = aX;
        y = aY;
    }

    public static Coord RandCoord(int aX, int aY) {
        return (new Coord((int) (Math.random() * aX), (int) (Math.random() * aY)));
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Coord)) {
            return false;
        }
        Coord c = (Coord) o;
        return (c.x == x && c.y == y);
    }


    @Override
    public String toString() {
        return "Coord{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}