package fxgames.dunslip;

import fxgames.Coord;

import java.util.*;
import java.util.function.Consumer;

import static fxgames.dunslip.Dunslip.ActionType.*;
import static fxgames.dunslip.Dunslip.Direction.*;
import static fxgames.dunslip.Dunslip.GamePiece.*;
import static fxgames.dunslip.Dunslip.GameState.*;

public class Dunslip {
    private int width;
    private int height;
    public int history_position = 0;
    Coord exit;

    List<Dunslip> history = new ArrayList<>();

    public enum GameState {design, preGame, inGame, beenEaten, postGame}

    public enum GamePiece {PLAYER, WALL, TREASURE, GOBLIN, PIT}

    GameState gameState;
    private transient List<Consumer<Dunslip>> consumers = new ArrayList<>();

    public enum Direction {UP, RIGHT, DOWN, LEFT}

    public record Thing(
            int id,
            GamePiece type,
            int x,
            int y,
            Direction blocks,
            boolean occupies,
            boolean removeOnTouch,
            boolean flees,
            boolean kills
    ) {
        public static Thing copy(Thing t) {
            return new Thing(t.id, t.type, t.x, t.y, t.blocks, t.occupies, t.removeOnTouch, t.flees, t.kills);
        }

        public static Thing move(Thing t, int x, int y) {
            return new Thing(t.id, t.type, x, y, t.blocks, t.occupies, t.removeOnTouch, t.flees, t.kills);
        }
    }

    public enum ActionType {MOVE, REMOVE, EXIT, TOUCHED, KILLED}

    public record Action(
            int turn,
            int ID,
            GamePiece it,
            ActionType type,
            Coord source,
            Coord dest) {
    }

    public List<Action> actions = new ArrayList<>();

    public record Effect(
            Thing effect,
            Thing cause,
            ActionType type
    ) {
    }

    public ArrayList<Effect> effectList = new ArrayList<>();

    public List<Thing> things = new ArrayList<>();

    private static int ID = 0;

    public static int getID() {
        ID++;
        return ID;
    }

    public int TURN = 0;

    public static Thing Wall(int x, int y, Direction dir) {
        return new Thing(getID(), GamePiece.WALL, x, y, dir, false, false, false, false);
    }

    public Dunslip(int w, int h) {
        width = w;
        height = h;
        gameState = GameState.inGame;
    }

    public Dunslip(Dunslip d) {
        this.width = d.width;
        this.height = d.height;
        this.exit = new Coord(d.exit.x, d.exit.y);
        this.gameState = d.gameState;
        for (Thing t : d.things) this.add(Thing.copy(t));
        d.consumers = this.consumers;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Dunslip d)) {
            return false;
        }
        return (d.width == width &&
                d.height == height &&
                d.exit.equals(exit) &&
                d.gameState == gameState &&
                d.things.equals(things) &&
                d.effectList.equals(effectList));
    }

    public void copy_history(int hp) {
        var d = history.get(hp);
        this.width = d.width;
        this.height = d.height;
        this.exit = new Coord(d.exit.x, d.exit.y);
        this.gameState = d.gameState;
        things.clear();
        for (Thing t : d.things) this.add(Thing.copy(t));
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

    public boolean add(Thing thing) {
        if (things.contains(thing)) return false;
        things.add(thing);
        return true;
    }

    public Thing player() {
        return things.stream().filter(thing -> thing.type == PLAYER).findFirst().get();
    }

    public boolean remove(Thing thing) {
        var l = things.indexOf(thing);
        if (l == -1) return false;
        things.remove(l);
        return true;
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

    public Direction deltaToDir(int x, int y) {
        if (x == 1) return RIGHT;
        if (x == -1) return LEFT;
        if (y == 1) return DOWN;
        return UP;
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

    public boolean moveThingOneSpace(Thing it, Direction d) {
        Coord dest = new Coord(it.x, it.y).add(dirToDelta(d));
        if (dest.x < 0 || dest.y < 0 || dest.x >= width || dest.y >= height) return false;
        for (int i = things.size() - 1; i >= 0; i--) {
            var thing = things.get(i);
            if (thing == it) continue;
            var thingLoc = new Coord(thing.x, thing.y);
            if ((it.x == thingLoc.x) && (it.y == thingLoc.y) && (thing.blocks == d)) return false;
            if (dest.equals(thingLoc)) {
                if (thing.removeOnTouch) effectList.add(new Effect(thing, it, TOUCHED));
                if (thing.occupies || (thing.blocks == directionComplement(d))) return false;
            }
        }
        return true;
    }

    private boolean onDeadlyGround(Thing self) {
        for (int i = things.size() - 1; i >= 0; i--) {
            var it = things.get(i);
            if (it == self || it.x != self.x || it.y != self.y) continue;
            if (it.kills) return true;
        }
        return false;
    }

    private boolean moveThing(Thing thing, Direction d) {
        var start = new Coord(thing.x, thing.y);
        while (moveThingOneSpace(thing, d)) {
            var delta = dirToDelta(d);
            var t = Thing.move(thing, thing.x + delta.x, thing.y + delta.y);
            remove(thing);
            add(t);
            thing = t;

            if (onDeadlyGround(thing)) {
                if (t.type == PLAYER) {
                    gameState = beenEaten;
                } else {
                    effectList.add(new Effect(t, null, KILLED));
                }
                break;
            }

            if (thing.x == exit.x && thing.y == exit.y && thing.type == PLAYER) {
                gameState = postGame;
                break;
            }
        }
        var end = new Coord(thing.x, thing.y);
        actions.add(new Action(TURN, thing.id, thing.type, ActionType.MOVE, start, end));
        return !start.equals(end);
    }

    public boolean movePlayer(Direction d) {
        if (gameState != GameState.inGame) return false;
        if (history.size() == 0) recordState();
        var moved = moveThing(player(), d);
        Dunslip before;
        do {
            before = new Dunslip(this);
            processEffects();
            afterEffects();
        } while (!this.equals(before));
        recordState();
        TURN++;
        return moved;
    }

    private void recordState() {
        if (history.size() > 0) history = new ArrayList<>(history.subList(0, history_position + 1));
        history.add(new Dunslip(this));
        history_position = history.size() - 1;
        ;
    }

    public void processEffects() {
        effectList.forEach(e -> {
            if (e.type == TOUCHED && e.effect.removeOnTouch)
                remove(e.effect);
            else if (e.type == KILLED)
                remove(e.effect);
        });
        effectList = new ArrayList<>();
    }

    private void afterEffects() {
        for (int i = things.size() - 1; i >= 0; i--) {
            var thing = things.get(i);
            var dx = thing.x - player().x;
            var dy = thing.y - player().y;
            if (Math.abs(dx) + Math.abs(dy) == 1) {
                if (thing.flees) flee(thing, deltaToDir(dx, dy));
            }
        }
    }

    private void flee(Thing thing, Direction dir) {
        moveThing(thing, dir);
    }

    @Override
    public String toString() {
        return "Dunslip{" +
                "width=" + width +
                ", height=" + height +
                ", history_position=" + history_position +
                ", exit=" + exit +
                ", \nhistory=" + history +
                ", \ngameState=" + gameState +
                ", consumers=" + consumers +
                ", effectList=" + effectList +
                ", things=" + things +
                '}';
    }
}
