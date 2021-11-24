package fxgames;

import java.io.Serializable;

public class Coord implements Serializable {
    public int x;
    public int y;

    public Coord(int aX, int aY) {
        x = aX;
        y = aY;
    }

    public static Coord RandCoord(int aX, int aY) {
        return (new Coord((int) (Math.random() * aX), (int) (Math.random() * aY)));
    }

    public Coord add(Coord operand) {
        return (new Coord(x+operand.x, y+operand.y));
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Coord c)) {
            return false;
        }
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