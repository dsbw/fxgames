package fxgames;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class TileController {

    public void openTTT(MouseEvent mouseEvent) {
       NodeController.me.activate("tic-tac-toe", (Pane) Main.me.bp.getCenter(),  event -> {
           Main.me.bp.setCenter(NodeController.me.node("tic-tac-toe"));
       });
    }
}
