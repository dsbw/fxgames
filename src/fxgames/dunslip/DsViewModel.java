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
import static fxgames.dunslip.Dunslip.GamePiece.wall;

public class DsViewModel {

    private final Dunslip game;
    private final Grid board;
    private final DunslipController cont;
    private final Circle playerToken;
    private final Circle exitToken;

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

        playerToken = new Circle();
        playerToken.setFill(Color.web("0x000077", 1.0));
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
                        if (!game.remove(x, y, thing))
                            game.add(x, y, thing);
                        draw();
                    }
                }
        );

        board.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                var oh = game.history.size();
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
                var valid = oh != game.history.size();
                if (Boolean.TRUE.equals(valid)) {
                    addTransition(new DsViewModel.Transition(playerToken, new Coord(game.player.x, game.player.y)));
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

    public Rectangle drawWall(Rectangle2D r) {
        Rectangle w = new Rectangle(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
        w.setFill(Color.web("0x353535", 1.0));
        board.piecePane.getChildren().add(w);
        return w;
    }

    public void addWall(int i, int j, Direction d) {
        var isHorz = (d == UP || d == DOWN);
        var isPlus = (d == RIGHT || d == DOWN);
        var r = drawWall(board.getWallDim(new Coord(i, j), isHorz, isPlus));
        walls.put(new CoordPair(
                new Coord(i, j),
                new Coord(i + (d == RIGHT ? +1 : d == LEFT ? -1 : 0),
                        j + (d == DOWN ? +1 : d == UP ? -1 : 0))), r);
    }

    public void draw() {
        walls = new HashMap<>();
        if (board.getHeight() == 0 || board.getWidth() == 0) return;
        board.setRowCount(game.getHeight());
        board.setColCount(game.getWidth());
        board.piecePane.getChildren().clear();

        for (var entry : game.things.entrySet()) {
            for (var thing : entry.getValue()) {
                switch (thing.id()) {
                    case wall -> addWall(entry.getKey().x, entry.getKey().y, thing.blocks());
                    case treasure -> {
                        var t = new Circle();
                        setToken(t, tokenCalc(t, new Coord(entry.getKey().x, entry.getKey().y)));
                        t.setFill(Color.GOLD);
                        board.piecePane.getChildren().add(t);
                    }
                }
            }
        }

        if (game.player != null)
            board.piecePane.getChildren().add(playerToken);
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
        setToken(playerToken, tokenCalc(playerToken, new Coord(game.player.x, game.player.y)));
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
        messageWin.setX(board.localToScreen(board.getBoundsInLocal()).getMinX() + board.cellLocalX(game.player.x));
        messageWin.setY(board.localToScreen(board.getBoundsInLocal()).getMinY() + board.cellLocalY(game.player.y));
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
