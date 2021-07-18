package fxgames;

import javafx.scene.layout.StackPane;

public class Grid extends StackPane {

    private int colCount;
    private int rowCount;

    public Grid(int columns, int rows) {
        colCount = columns;
        rowCount = rows;
    }

    public int boundCol(double v) {
        return (v < 0) ? 0 : (v >= colCount) ? colCount - 1 : (int) v;
    }

    public int boundRow(double v) {
        return (v < 0) ? 0 : (v > rowCount) ? rowCount - 1 : (int) v;
    }

    public double colWidth() {
        return widthProperty().doubleValue() / colCount;
    }

    public double rowHeight() {
        return heightProperty().doubleValue() / rowCount;
    }

    public Coord getCoord(double x, double y) {
        return new Coord(boundCol(x % colWidth()), boundRow(y % rowHeight()));
    }

}
