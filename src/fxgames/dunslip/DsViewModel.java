package fxgames.dunslip;

import fxgames.Coord;
import fxgames.CoordPair;
import fxgames.Grid;
import fxgames.Main;
import javafx.animation.*;
import javafx.beans.InvalidationListener;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.util.Duration;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;

import static fxgames.dunslip.Dunslip.*;
import static fxgames.dunslip.Dunslip.Direction.*;
import static fxgames.dunslip.Dunslip.GamePiece.LIGHT;

public class DsViewModel {

    private final Dunslip game;
    private final Grid board;
    private final DunslipController cont;
    private final Circle exitToken;
    private final HashMap<Integer, Circle> tokens = new HashMap<>();

    public record Transition(Circle token, Coord c) {
    }

    private final LinkedList<DsViewModel.Transition> transitionQueue = new LinkedList<>();
    private Timeline timeline = new Timeline();
    private final Popup messageWin = new Popup();
    private final Label messageText = new Label("Victory is yours!");
    private HashMap<CoordPair, Rectangle> walls;

    public Thing thing;

    public DsViewModel(Grid g, Dunslip d, DunslipController c) {
        game = d;
        board = g;
        cont = c;

        exitToken = new Circle();
        exitToken.setFill(Color.web("0xFF0077", 1.0));

        board.setColCount(game.getWidth());
        board.setRowCount(game.getHeight());
        board.setHoverColor(null);

        game.addConsumer(dunslip -> draw());

        InvalidationListener l = observable -> resize();
        g.widthProperty().addListener(l);
        g.heightProperty().addListener(l);

        messageWin.getContent().add(messageText);
        messageText.setMinWidth(100);
        messageText.setMinHeight(20);

        board.setOnGridMouseClicked((var1, x, y) -> {
                    System.out.println(thing);
                    if (thing != null) {
                        var t = Thing.move(thing, x, y);
                        if (!game.remove(t))
                            game.add(Thing.move(t, x, y));
                        draw();
                    }
                }
        );

        board.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                var oh = game.history.size();
                var op = game.history_position;
                var ot = game.TURN;
                var moved =
                        switch (keyEvent.getCode()) {
                            case UP -> game.movePlayer(UP);
                            case RIGHT -> game.movePlayer(RIGHT);
                            case LEFT -> game.movePlayer(LEFT);
                            case DOWN -> game.movePlayer(DOWN);
                            case R -> game.rewind();
                            case F -> game.fforward();
                            default -> null;
                        };
                var valid = (oh != game.history.size()) || (op != game.history_position);
                if (Boolean.TRUE.equals(valid)) {
                    for (var i = 0; i < game.actions.size(); i++) {
                        var a = game.actions.get(i);
                        if (ot == a.turn() && a.it() != LIGHT) {
                            addTransition(new Transition(tokens.get(a.ID()), a.dest()));
                        }
                    }
                    if (game.gameState == GameState.postGame) {
                        messageWin.show(Main.me.stage);
                        showMessage("You have escaped!");
                    } else if (game.gameState == GameState.beenEaten) {
                        messageWin.show(Main.me.stage);
                        showMessage("You have been eaten!");
                    }
                } else if (Boolean.FALSE.equals(valid)) Toolkit.getDefaultToolkit().beep();
                keyEvent.consume();
            }
        });
    }

    public void addTransition(DsViewModel.Transition t) {
        transitionQueue.add(t);
        playTransition();
    }

    public void removeTransition() {
        if (transitionQueue.size() > 0)
            transitionQueue.remove(0);
        playTransition();
    }

    public void playTransition() {
        if (timeline.getStatus() != Animation.Status.RUNNING && transitionQueue.size() > 0) {
            var t = transitionQueue.get(0).token;
            var n = tokenCalc(t, transitionQueue.get(0).c);
            Duration duration = Duration.millis(125);
            timeline = new Timeline(
                    new KeyFrame(duration, new KeyValue(t.centerXProperty(), n.getX(), Interpolator.EASE_IN)),
                    new KeyFrame(duration, new KeyValue(t.centerYProperty(), n.getY(), Interpolator.EASE_IN)));
            timeline.setOnFinished((e) -> {
                removeTransition();
                if (transitionQueue.size() == 0) draw();
            });
            timeline.play();
        }
    }

    public Rectangle drawWall(Rectangle2D r, Color c) {
        Rectangle w = new Rectangle(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
        w.setFill(c);
        board.piecePane.getChildren().add(w);
        return w;
    }

    public void addWall(int i, int j, Direction d, Color c) {
        var isHorz = (d == UP || d == DOWN);
        var isPlus = (d == RIGHT || d == DOWN);
        var r = drawWall(board.getWallDim(new Coord(i, j), isHorz, isPlus), c);
        walls.put(new CoordPair(
                new Coord(i, j),
                new Coord(i + (d == RIGHT ? +1 : d == LEFT ? -1 : 0),
                        j + (d == DOWN ? +1 : d == UP ? -1 : 0))), r);
    }

    public void addWall(int i, int j, Direction d) {
        addWall(i, j, d, (Color.web("0x353535", 1.0)));
    }

    public Circle addPiece(Coord loc, Color c) {
        var t = new Circle();
        setToken(t, tokenCalc(t, new Coord(loc.x, loc.y)));
        t.setFill(c);
        board.piecePane.getChildren().add(t);
        return t;
    }

    public void draw() {
        walls = new HashMap<>();
        if (board.getHeight() == 0 || board.getWidth() == 0) return;
        board.setRowCount(game.getHeight());
        board.setColCount(game.getWidth());
        board.piecePane.getChildren().clear();
        tokens.clear();

        for (var thing : game.things) {
            switch (thing.type()) {
                case WALL -> addWall(thing.x(), thing.y(), thing.blocks());
                case TREASURE -> tokens.put(thing.id(), addPiece(new Coord(thing.x(), thing.y()), Color.GOLD));
                case GOBLIN -> tokens.put(thing.id(), addPiece(new Coord(thing.x(), thing.y()), Color.WHITE));
                case HOBGOBLIN -> {
                    addWall(thing.x(), thing.y(), thing.blocks(), Color.RED);
                    tokens.put(thing.id(), addPiece(new Coord(thing.x(), thing.y()), Color.BLUEVIOLET));
                }
                case EVIL_EYE -> {
                    addWall(thing.x(), thing.y(), thing.blocks(), Color.BLUE);
                    tokens.put(thing.id(), addPiece(new Coord(thing.x(), thing.y()), Color.GREEN));
                }
                case PIT -> tokens.put(thing.id(), addPiece(new Coord(thing.x(), thing.y()), Color.web("0x000000")));
                case PLAYER -> tokens.put(thing.id(), addPiece(new Coord(thing.x(), thing.y()), Color.web("0x000077")));
            }
        }

        if (game.exit != null)
            board.piecePane.getChildren().add(exitToken);

        board.requestFocus();
    }

    public javafx.geometry.Point2D tokenCalc(Circle token, Coord c) {
        var r = board.getCellDim(c);
        var newX = r.getMinX() + r.getWidth() / 2;
        var newY = r.getMinY() + r.getHeight() / 2;
        token.setRadius(Math.min(r.getWidth(), r.getHeight()) / 2);
        return new javafx.geometry.Point2D((float) newX, (float) newY);
    }

    public void resize() {
        if (board.getHeight() == 0 || board.getWidth() == 0) return;
        setToken(exitToken, tokenCalc(exitToken, new Coord(game.exit.x, game.exit.y)));
        draw();
    }

    public void setToken(Circle token, javafx.geometry.Point2D n) {
        token.setCenterX(n.getX());
        token.setCenterY(n.getY());
    }

    private void showMessage(String text) {
        messageText.setText(text);
        messageText.getStyleClass().add("popup-message");
        messageWin.setOpacity(1.0);
        messageWin.setX(board.localToScreen(board.getBoundsInLocal()).getMinX() + board.cellLocalX(game.player().x()));
        messageWin.setY(board.localToScreen(board.getBoundsInLocal()).getMinY() + board.cellLocalY(game.player().y()));
        messageWin.show(Main.me.stage);
        Timeline tl = new Timeline();
        KeyValue kv = new KeyValue(messageWin.opacityProperty(), 0.0);
        KeyFrame kf = new KeyFrame(Duration.millis(3000), kv);
        tl.getKeyFrames().addAll(kf);
        tl.setOnFinished(e -> {
            messageWin.hide();
        });
        tl.play();
    }

}
