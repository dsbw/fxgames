package fxgames.dunslip;

import fxgames.Coord;

import java.util.*;
import java.util.function.Consumer;

import static fxgames.dunslip.Dunslip.Direction.*;
import static fxgames.dunslip.Dunslip.GameState.postGame;

public class Dunslip {
    private final int width;
    private final int height;
    Coord player;
    Coord exit;

    public enum GameState {design, preGame, inGame, beenEaten, postGame}
    public enum GamePiece {wall}

    GameState gameState;
    private transient List<Consumer<Dunslip>> consumers = new ArrayList<>();

    public enum Direction {UP, RIGHT, DOWN, LEFT}

    public record Thing(
            GamePiece id,
            Direction blocks
            ) { }

    public HashMap<Coord, List<Thing>> things = new HashMap<>();

    public Dunslip(int w, int h) {
        width = w;
        height = h;
        gameState = GameState.inGame;
    }

    public boolean add(int x, int y, Thing thing) {
        var c = new Coord(x, y);
        things.computeIfAbsent(c, k -> new ArrayList<>());
        var l = things.get(new Coord(x, y));
        if(l.contains(thing)) return false;
        l.add(thing);
        return true;
    }

    public boolean remove(int x, int y, Thing thing) {
        var c = new Coord(x, y);
        var l = things.get(c);
        if(l==null) return false;
        return (l.remove(thing));
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

    public void addConsumer(Consumer<Dunslip> l) {
        if (consumers == null) consumers = new ArrayList<>();
        consumers.add(l);
    }

    public void removeConsumer(Consumer<Dunslip> l) {
        consumers.remove(l);
    }

    public void alertConsumers() {
        consumers.forEach(c -> c.accept(this));
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Direction directionComplement(Direction dir) {
        return switch (dir) {
            case UP -> DOWN;
            case DOWN -> UP;
            case RIGHT -> LEFT;
            case LEFT -> RIGHT;
        };
    }

    public boolean movePlayerOneSpace(Direction d) {
        Coord c = player.add(dirToDelta(d));
        if (c.x < 0 || c.y < 0 || c.x >= width || c.y >= height) return false;
        for (var entry : things.entrySet()) {
            if (entry.getKey().equals(player) &&
                    entry.getValue().stream().anyMatch(thing -> thing.blocks == d)) return false;
            if (entry.getKey().equals(c) &&
                    entry.getValue().stream().anyMatch(thing -> thing.blocks == directionComplement(d))) return false;
        }
        return true;
    }

    public boolean movePlayer(Direction d) {
        if (gameState != GameState.inGame) return false;
        var moved = false;
        while (movePlayerOneSpace(d)) {
            var delta = dirToDelta(d);
            player.x += delta.x;
            player.y += delta.y;
            moved = true;
            alertConsumers();
            if (player.equals(exit)) {
                gameState = postGame;
                break;
            }
        }
        return moved;
    }


}
