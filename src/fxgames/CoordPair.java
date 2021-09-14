package fxgames;

public class CoordPair {
    private final Coord c1;
    private final Coord c2;

    public CoordPair(Coord c, Coord d) {
        c1 = new Coord(c.x, c.y);
        c2 = new Coord(d.x, d.y);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof CoordPair)) {
            return false;
        }
        CoordPair cp = (CoordPair) o;
        return (cp.c1.equals(c1) && cp.c2.equals(c2)) ||
                (cp.c1.equals(c2) && cp.c2.equals(c1));
    }

    @Override
    public int hashCode() {
        return c1.x*c2.x*c1.y*c2.y;
    }
}

