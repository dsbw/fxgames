package fxgames;

@FunctionalInterface
public interface GridDragMove<T> {
    public boolean handle(T var1, int x, int y);
}