package fxgames.dunslip;

import fxgames.Coord;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static fxgames.dunslip.Dunslip.GameState.postGame;

public class Dunslip {
    private final int width;
    private final int height;
    Coord player;
    Coord exit;

    public enum GameState {design, preGame, inGame, beenEaten, postGame}

    GameState gameState;
    private transient List<Consumer<Dunslip>> consumers = new ArrayList<>();

    public enum Direction {UP, RIGHT, DOWN, LEFT}

    public Dunslip(int w, int h) {
        width = w;
        height = h;
        gameState = GameState.inGame;
    }

    public Coord dirToDelta(Dunslip.Direction dir) {
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

    public boolean movePlayerOneSpace(Direction d) {
        Coord c = player.add(dirToDelta(d));
        if (c.x < 0 || c.y < 0 || c.x >= width || c.y >= height) return false;
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
