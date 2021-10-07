package fxgames.basicmaze;

import fxgames.Coord;
import fxgames.CoordPair;

import java.io.Serializable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static fxgames.Coord.RandCoord;
import static java.lang.Thread.sleep;

public class BasicMaze implements Serializable {
    private int width;
    private int height;
    public int slots;
    public Coord player;
    public Coord entrance;
    public Coord exit;

    public enum Direction {UP, RIGHT, DOWN, LEFT}

    private EnumSet<Direction>[][] maze;

    private transient List<Consumer<BasicMaze>> consumers = new ArrayList<>();
    private transient List<Consumer<CoordPair>> updaters = new ArrayList<>();

    public BasicMaze(int w, int h) {
        width = w;
        height = h;
    }

    public void reset(int w, int h) {
        width = w;
        height = h;
        maze = null;
        alertConsumers();
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

    public void addUpdater(Consumer<CoordPair> cp) {
        if (updaters == null) updaters = new ArrayList<>();
        updaters.add(cp);
    }

    public void alertUpdaters(CoordPair cp) {
        updaters.forEach(c -> c.accept(cp));
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
        slots = width * height;
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
                var c2 = connect.apply(c, d);
                alertConsumers();
                alertUpdaters(new CoordPair(c, c2));
                try {
                    sleep(0, 1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                c = c2;
                slots--;
            } else do {
                c = RandCoord(width, height);
            } while (maze[c.y][c.x].isEmpty());
        } while (slots > 1);
        System.out.println("DONE!");
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public EnumSet<Direction> get(int y, int x) {
        if (maze == null) return EnumSet.noneOf(Direction.class);
        return maze[y][x];
    }

    public void assignCoords () {
        entrance = Coord.RandCoord(width, height);
        while(exit==null || exit.equals(entrance)) exit = Coord.RandCoord(width, height);
        player = new Coord(entrance.x, entrance.y);
    }

    public boolean movePlayer(Direction d) {
        if(get(player.y, player.x).contains(d)) {
            var delta = dirToDelta(d);
            player.x += delta.x;
            player.y += delta.y;
            alertConsumers();
            return true;
        } else return false;
    }

    public void print() {
        if (maze == null) {
            System.out.println("-----no-maze-exists----");
            return;
        }
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
