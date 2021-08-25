package fxgames;

import javafx.scene.input.DragEvent;

@FunctionalInterface
public interface GridDragDrop {
    public void handle(DragEvent var1, int x, int y);
}