package fxgames.ttt;

import fxgames.Grid;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.GridPane;

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
    }

    public String dragBoardToPiece(DragEvent de) {
        return de.getDragboard().getString();
    }

    public void draw() {
        board.piecePane.getChildren().removeAll();

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
    }

}
