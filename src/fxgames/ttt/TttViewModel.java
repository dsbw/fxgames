package fxgames.ttt;

import fxgames.Grid;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.shape.Line;

import java.util.Objects;

public class TttViewModel {

    private final TicTacToe game;
    private final Grid board;
    private final TttController cont;

    public TttViewModel(Grid g, TicTacToe t, TttController c) {
        game = t;
        board = g;
        cont = c;

        g.setOnGridDragMove((var1, x, y) -> game.canAccept(dragBoardToPiece(var1), x, y));

        g.setOnGridDragDrop((var1, x, y) -> {
            game.addPiece(dragBoardToPiece(var1), x, y);
            draw();
        });

        game.addConsumer((game) -> this.draw());
    }

    public String dragBoardToPiece(DragEvent de) {
        return de.getDragboard().getString();
    }

    public void draw() {
        board.piecePane.getChildren().clear();

        for (var i = 0; i < 3; i++) {
            for (var j = 0; j < 3; j++)
                if (game.get(i, j) != null) {
                    ImageView xo = new ImageView();

                    if (game.get(i, j).equals("X"))
                        xo.setImage(cont.X.getImage());
                    else if (game.get(i, j).equals("O"))
                        xo.setImage(cont.O.getImage());
                    xo.setFitWidth(board.cellWidth());
                    xo.setFitHeight(board.cellHeight());
                    xo.setX(board.cellLocalX(i));
                    xo.setY(board.cellLocalY(j));
                    board.piecePane.getChildren().addAll(xo);
                }
        }
        if (Objects.equals(game.winner, "X") || Objects.equals(game.winner, "O"))
            drawVictorySlash();
    }

    public void drawVictorySlash() {
        var wxs = 0.0;
        var wys = 0.0;
        var wxe = board.getWidth();
        var wye = board.getHeight();

        Line line = new Line();
        board.piecePane.getChildren().add(line);
        line.getStyleClass().add("line");
        if (game.vxd == 1 && game.vyd == 1) {
            //don't actually have to do anything since these are the defaults, but lets leave the case in here
        } else if (game.vxd == -1 && game.vyd == 1) {
            wys = board.getHeight();
            wye = 0;
        } else if (game.vyd == 1) {
            wys = (game.vx * board.cellWidth()) + (board.cellWidth() / 2);
            wye = wys;
        } else if (game.vxd == 1) {
            wxs = (game.vy * board.cellHeight()) + (board.cellHeight() / 2);
            wxe = wxs;
        }
        line.setStartX(wxs);
        line.setStartY(wys);
        line.setEndX(wxe);
        line.setEndY(wye);
    }
}


