package fxgames.dunslip;

import fxgames.Coord;

import java.util.*;
import java.util.function.Consumer;

import static fxgames.dunslip.Dunslip.Direction.*;
import static fxgames.dunslip.Dunslip.GameState.postGame;

public class Dunslip {
    private int width;
    private int height;
    private int history_position = 0;
    Coord player;
    Coord exit;

    List<Dunslip> history = new ArrayList<>();

    public enum GameState {design, preGame, inGame, beenEaten, postGame}

    public enum GamePiece {wall, treasure}

    GameState gameState;
    private transient List<Consumer<Dunslip>> consumers = new ArrayList<>();

    public enum Direction {UP, RIGHT, DOWN, LEFT}

    public record Thing(
            GamePiece id,
            Direction blocks,
            Boolean occupies,
            Boolean removeOnTouch
    ) {
    }

    public record Effect(
            Coord effectLoc,
            Thing effect,
            Coord causeLoc,
            Thing cause
    ) {
    }

    public ArrayList<Effect> effectList;

    public HashMap<Coord, List<Thing>> things = new HashMap<>();

    public static Thing Wall(Direction dir) {
        return new Thing(GamePiece.wall, dir, false, false);
    }

    public Dunslip(int w, int h) {
        width = w;
        height = h;
        gameState = GameState.inGame;
    }

    public Dunslip(Dunslip d) {
        this.width = d.width;
        this.height = d.height;
        this.player = new Coord(d.player.x, d.player.y);
        this.exit = new Coord(d.exit.x, d.exit.y);
        this.gameState = d.gameState;
        for (Map.Entry<Coord, List<Thing>> entry : d.things.entrySet()) {
            for (Thing t : entry.getValue()) {
                this.add(entry.getKey().x, entry.getKey().y, new Thing(t.id, t.blocks, t.occupies, t.removeOnTouch));
            }
        }
        if (d.effectList != null) {
            this.effectList = new ArrayList<Effect>();
            for (Effect e : d.effectList) {
                this.effectList.add(new Effect(e.effectLoc, e.effect, e.causeLoc, e.cause));
            }
        }
        d.consumers = this.consumers;
    }

    public void copy_history(int hp) {
        var d = history.get(hp);
        this.width = d.width;
        this.height = d.height;
        this.player = new Coord(d.player.x, d.player.y);
        this.exit = new Coord(d.exit.x, d.exit.y);
        this.gameState = d.gameState;
        for (Map.Entry<Coord, List<Thing>> entry : d.things.entrySet()) {
            for (Thing t : entry.getValue()) {
                this.add(entry.getKey().x, entry.getKey().y, new Thing(t.id, t.blocks, t.occupies, t.removeOnTouch));
            }
        }
        if (d.effectList != null) {
            this.effectList = new ArrayList<Effect>();
            for (Effect e : d.effectList) {
                this.effectList.add(new Effect(e.effectLoc, e.effect, e.causeLoc, e.cause));
            }
        }
        processEffects();
        this.consumers = d.consumers;
    }

    public Boolean rewind() {
        if (history_position == 0) return false;
        history_position--;
        copy_history(history_position);
        System.out.printf("<<< SIZE: %d, POSITION %d => %s \n", history.size(), history_position, history);
        return true;
    }

    public Boolean fforward() {
        if (history_position == history.size() - 1) return false;
        history_position++;
        copy_history(history_position);
        System.out.printf(">>> SIZE: %d, POSITION %d => %s \n", history.size(), history_position, history);
        return true;
    }

    public boolean add(int x, int y, Thing thing) {
        var c = new Coord(x, y);
        things.computeIfAbsent(c, k -> new ArrayList<>());
        var l = things.get(new Coord(x, y));
        if (l.contains(thing)) return false;
        l.add(thing);
        return true;
    }

    public boolean remove(int x, int y, Thing thing) {
        var c = new Coord(x, y);
        var l = things.get(c);
        if (l == null) return false;
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
            entry.getValue().stream()
                    .filter(thing -> entry.getKey().equals(c) && thing.removeOnTouch)
                    .forEach(thing -> {
                        effectList.add(new Effect(c, thing, player, null));
                    });
            if (entry.getKey().equals(c) &&
                    entry.getValue().stream().anyMatch(thing -> thing.occupies)) return false;
        }
        return true;
    }

    public boolean movePlayer(Direction d) {
        if (gameState != GameState.inGame) return false;
        if (history.size() == 0) recordState();
        var moved = false;
        effectList = new ArrayList<>();
        while (movePlayerOneSpace(d)) {
            var delta = dirToDelta(d);
            player.x += delta.x;
            player.y += delta.y;
            moved = true;
            if (player.equals(exit)) {
                gameState = postGame;
                break;
            }
        }
        processEffects();
        recordState();
        return moved;
    }

    private void recordState() {
        if (history.size() > 0) history = new ArrayList<>(history.subList(0, history_position + 1));
        history.add(new Dunslip(this));
        history_position = history.size() - 1;;
    }

    public void processEffects() {
        effectList.forEach(e -> {
            if (e.effect.removeOnTouch)
                remove(e.effectLoc.x, e.effectLoc.y, e.effect);
        });
    }


}
