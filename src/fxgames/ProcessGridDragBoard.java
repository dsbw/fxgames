package fxgames;

import javafx.scene.input.DragEvent;

@FunctionalInterface
public interface ProcessGridDragBoard<T> {
    public T handle(DragEvent d);
}