package fxgames.basicmaze;

import fxgames.Coord;
import fxgames.CoordPair;
import fxgames.Grid;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.function.Consumer;

import fxgames.basicmaze.BasicMaze.Direction;
import javafx.scene.shape.Shape;

import static fxgames.basicmaze.BasicMaze.Direction.*;

public class MazeViewModel {
    private final BasicMaze game;
    private final Grid board;
    private final MazeController cont;
    private HashMap<CoordPair, Rectangle> walls;
    public int calls = 0;
    Consumer<BasicMaze> drawFn = (game) -> this.draw();

    public MazeViewModel(Grid g, BasicMaze m, MazeController c) {
        game = m;
        board = g;
        cont = c;

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
                System.out.println(keyEvent);
                var consume = true;
                switch (keyEvent.getCode()) {
                    case UP -> game.movePlayer(UP);
                    case RIGHT -> game.movePlayer(RIGHT);
                    case LEFT -> game.movePlayer(LEFT);
                    case DOWN -> game.movePlayer(DOWN);
                    default -> consume = false;
                }
                if(consume) keyEvent.consume();
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
        if(game.slots==2)
            game.addConsumer(drawFn);
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

        if (game.player != null) {
            var r = board.getCellDim(game.player);
            var player = new Circle(r.getMinX()+r.getWidth()/2, r.getMinY()+r.getHeight()/2, Math.min(r.getWidth(), r.getHeight())/2);
            player.setFill(Color.web("0x000077", 1.0));
            board.piecePane.getChildren().add(player);
        }

    }

}
