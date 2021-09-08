package fxgames.basicmaze;

import fxgames.Coord;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static fxgames.Coord.RandCoord;

public class BasicMaze {
    private final int width;
    private final int height;

    public enum Direction {UP, RIGHT, DOWN, LEFT}

    private EnumSet<Direction>[][] maze;

    private transient List<Consumer<BasicMaze>> consumers = new ArrayList<>();

    public BasicMaze(int w, int h) {
        width = w;
        height = h;
    }

    public void addConsumer(Consumer<BasicMaze> l) {
        if (consumers == null) consumers = new ArrayList<>();
        consumers.add(l);
    }

    public void removeConsumer(Consumer<BasicMaze> l) {
        consumers.remove(l);
    }

    public void alertConsumers() {
        consumers.forEach(c -> c.accept(this));
    }

    public Coord dirToDelta(Direction dir) {
        int mx = 0;
        int my = 0;
        switch (dir) {
            case UP -> my = -1;
            case DOWN -> my = 1;
            case RIGHT -> mx = 1;
            case LEFT -> mx = -1;
        }
        return new Coord(mx, my);
    }

    public void generate() {
        int slots = width * height;
        maze = (EnumSet<Direction>[][]) new EnumSet<?>[height][width];
        for (int j = 0; j < height; j++)
            for (int i = 0; i < width; i++)
                maze[j][i] = EnumSet.noneOf(Direction.class);

        BiFunction<Coord, Direction, EnumSet<Direction>> neighbor = (co, dir) -> {
            Coord delta = dirToDelta(dir);
            Coord dest = new Coord(co.x + delta.x, co.y + delta.y);
            if (dest.y < 0 || dest.y >= height || dest.x < 0 || dest.x >= width) return null;
            else return maze[dest.y][dest.x];
        };

        Function<Direction, Direction> compDir = (dir) -> switch (dir) {
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case RIGHT -> Direction.LEFT;
            case LEFT -> Direction.RIGHT;
        };

        BiFunction<Coord, Direction, Coord> connect = (src, dir) -> {
            Coord dc = dirToDelta(dir);
            Coord dst = new Coord(src.x + dc.x, src.y + dc.y);
            maze[src.y][src.x].add(dir);
            maze[dst.y][dst.x].add(compDir.apply(dir));
            return dst;
        };

        Coord c = RandCoord(width, height);
        do {
            var l = new ArrayList<Direction>();
            for (Direction dir : Direction.values()) {
                EnumSet<Direction> n = neighbor.apply(c, dir);
                if (n != null && n.isEmpty())
                    l.add(dir);
            }

            if (l.size() > 0) {
                var d = l.get((new Random()).nextInt(l.size()));
                c = connect.apply(c, d);
                slots--;
            } else do {
                c = RandCoord(width, height);
            } while (maze[c.y][c.x].isEmpty());
        } while (slots > 1);
    }

    public void print() {
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                System.out.print((maze[j][i].contains(Direction.UP)) ? "·░·" : "·─·");
            }
            System.out.println();
            for (int i = 0; i < width; i++) {
                System.out.print((maze[j][i].contains(Direction.LEFT)) ? "░" : "|");
                System.out.print("░");
                System.out.print((maze[j][i].contains(Direction.RIGHT)) ? "░" : "|");
            }
            System.out.println();
            if (j == (height - 1)) {
                for (int i = 0; i < width; i++) {
                    System.out.print((maze[j][i].contains(Direction.DOWN)) ? "·░·" : "·─·");
                }
                System.out.println();
            }
        }
    }
}
