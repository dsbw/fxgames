package fxgames.basicmaze;

import fxgames.Coord;
import fxgames.CoordPair;
import fxgames.Grid;
import fxgames.Main;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.*;
import java.util.function.Consumer;

import fxgames.basicmaze.BasicMaze.Direction;
import javafx.stage.Popup;
import javafx.util.Duration;

import static fxgames.basicmaze.BasicMaze.Direction.*;

public class MazeViewModel {
    private final BasicMaze game;
    private final Grid board;
    private final MazeController cont;
    private HashMap<CoordPair, Rectangle> walls;
    public int calls = 0;
    Consumer<BasicMaze> drawFn = (game) -> this.draw();
    private final Popup messageWin = new Popup();
    private final Label messageText = new Label("Victory is yours!");
    private final Circle playerToken;
    private TranslateTransition playerTransition = new TranslateTransition();
    private final LinkedList<Coord> transitionQueue = new LinkedList<Coord>();

    public MazeViewModel(Grid g, BasicMaze m, MazeController c) {
        game = m;
        board = g;
        cont = c;

        playerToken = new Circle();
        playerToken.setFill(Color.web("0x000077", 1.0));

        messageWin.getContent().add(messageText);
        messageText.setMinWidth(100);
        messageText.setMinHeight(20);

        g.setColCount(m.getWidth());
        g.setRowCount(m.getHeight());

        InvalidationListener l = new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                draw();
            }
        };

        g.widthProperty().addListener(l);
        g.heightProperty().addListener(l);

        game.addUpdater(this::removeWall);

        board.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                /*if(playerTransition.getStatus()==Animation.Status.RUNNING) {
                    System.out.println("Too fast!");
                    keyEvent.consume(); //N.B. we're consuming all keystrokes here, not just cursor keys.
                    return;
                };*/
                var consume = true;
                var oldx = game.player.x;
                var oldy = game.player.y;
                switch (keyEvent.getCode()) {
                    case UP -> game.movePlayer(UP);
                    case RIGHT -> game.movePlayer(RIGHT);
                    case LEFT -> game.movePlayer(LEFT);
                    case DOWN -> game.movePlayer(DOWN);
                    default -> consume = false;
                }
                if (oldx != game.player.x || oldy != game.player.y)
                    addTransition(new Coord(game.player.x, game.player.y));
                if (consume) {
                    keyEvent.consume();

                    if (game.gameState == BasicMaze.GameState.postGame) {
                        messageWin.show(Main.me.stage);
                        showMessage("You have escaped!");
                    }
                }
            }
        });
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

    public void removeWall(CoordPair c) {
        Platform.runLater(() -> board.piecePane.getChildren().remove(walls.get(c)));
        if (game.slots == 2)
            game.addConsumer(drawFn);
    }

    public javafx.geometry.Point2D playerTokenCalc(Coord c) {
        var r = board.getCellDim(c);
        var newX = r.getMinX() + r.getWidth() / 2;
        var newY = r.getMinY() + r.getHeight() / 2;
        playerToken.setRadius(Math.min(r.getWidth(), r.getHeight()) / 2);
        return new javafx.geometry.Point2D((float)newX, (float)newY);
    }

    public void setPlayerToken(javafx.geometry.Point2D n) {
        playerToken.setTranslateX(0);
        playerToken.setTranslateY(0);
        playerToken.setCenterX(n.getX());
        playerToken.setCenterY(n.getY());
    }

    public void addTransition(Coord t) {
        transitionQueue.add(t);
        playTransition();
    }

    public void removeTransition() {
        if (transitionQueue.size() > 0)
            transitionQueue.remove(0);
        playTransition();
    }

    public void playTransition() {
        if (playerTransition.getStatus() != Animation.Status.RUNNING && transitionQueue.size() > 0) {
            var n = playerTokenCalc(transitionQueue.get(0));
            TranslateTransition translate = new TranslateTransition();
            translate.setByX(n.getX() - playerToken.getCenterX());
            translate.setByY(n.getY() - playerToken.getCenterY());
            translate.setDuration(Duration.millis(60));
            translate.setNode(playerToken);
            translate.setOnFinished((e) -> {
                setPlayerToken(n);
                removeTransition();
            });
            playerTransition = translate;
            translate.play();
        }
    }

    public void draw() {
        walls = new HashMap<CoordPair, Rectangle>();
        board.setRowCount(game.getHeight());
        board.setColCount(game.getWidth());
        board.piecePane.getChildren().clear();
        calls++;
        //System.out.printf("%d games slots on cycle %d\n",game.slots, calls);

        for (int j = 0; j < game.getHeight(); j++) {
            for (int i = 0; i < game.getWidth(); i++) {
                if (!game.get(j, i).contains(UP))
                    addWall(i, j, UP);
                if (j == (game.getHeight() - 1))
                    addWall(i, j, DOWN);
                if (!game.get(j, i).contains(BasicMaze.Direction.LEFT))
                    addWall(i, j, BasicMaze.Direction.LEFT);
                if (i == (game.getWidth() - 1))
                    addWall(i, j, RIGHT);
            }
        }

        if (game.entrance != null) {
            var r = board.getCellDim(game.entrance);
            var entrance = new Rectangle(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
            entrance.setFill(Color.web("0x770000", 1.0));
            board.piecePane.getChildren().add(entrance);
        }

        if (game.exit != null) {
            var r = board.getCellDim(game.exit);
            var exit = new Rectangle(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
            exit.setFill(Color.web("0x007700", 1.0));
            board.piecePane.getChildren().add(exit);
        }

        if (game.player != null)
            board.piecePane.getChildren().add(playerToken);
    }

    private void showMessage(String text) {
        messageText.setText(text);
        messageText.setStyle("-fx-text-fill: black; -fx-font-size: 18pt; -fx-font-weight: bold;");
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
