package fxgames;

import javafx.scene.input.MouseEvent;

public class TileController {

    public void openGame(String name) {
        NodeController.me.activate(name, Controller.me.central);
    }

    public void openTTT(MouseEvent mouseEvent) {
        openGame("tic-tac-toe");
    }

    public void openDunSlip(MouseEvent mouseEvent) {
        openGame("dungeon-slippers");
    }
}
