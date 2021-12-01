package fxgames;

import javafx.scene.input.MouseEvent;

@FunctionalInterface
public interface GridMouseEvent {
    public void handle(MouseEvent var1, int x, int y);
}
