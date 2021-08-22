package fxgames;

@FunctionalInterface
public interface GridDragDrop<T> {
    public void handle(T var1, int x, int y);
}