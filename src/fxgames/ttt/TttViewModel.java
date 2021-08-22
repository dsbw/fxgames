package fxgames.ttt;

import fxgames.Grid;

public class TttViewModel {

    private final TicTacToe game;

    public TttViewModel(Grid<String> g, TicTacToe t) {
        game = t;
        g.setProcessDragBoard(de -> de.getDragboard().getString());
        g.setOnGridDragMove(game::canAccept);
        g.setOnGridDragDrop(game::addPiece);
    }
}
