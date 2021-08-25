package fxgames;

import javafx.scene.input.DragEvent;

@FunctionalInterface
public interface GridDragMove {
    public boolean handle(DragEvent var1, int x, int y);
}