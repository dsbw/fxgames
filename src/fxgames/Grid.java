package fxgames;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class Grid extends StackPane {

    InvalidationListener redraw = observable -> setBackgrounds();


    public Grid() {
        this(2, 2);
    }

    private final SimpleIntegerProperty _colCount;
    private final SimpleIntegerProperty _rowCount;
    private ObjectProperty<Coord> _hoverCoord = null;

    public Grid(int columns, int rows) {
        _colCount = new SimpleIntegerProperty(2, "colCount");
        _rowCount = new SimpleIntegerProperty(rows, "rowCount");

        widthProperty().addListener(redraw);
        heightProperty().addListener(redraw);
        _colCount.addListener(redraw);
        _rowCount.addListener(redraw);

        this.setOnMouseEntered(event -> {
            _hoverCoord = new SimpleObjectProperty<Coord>(getCoord(event.getX(), event.getX()));
            _hoverCoord.addListener(redraw);
        });

        this.setOnMouseExited(event -> {
            _hoverCoord = null;
        });

        this.setOnMouseMoved(event -> {
            Coord c = getCoord(event.getX(), event.getY());
            Coord d = _hoverCoord.get();
            if(c.x!=d.x || c.y!=d.y) {
                _hoverCoord.set(getCoord(event.getX(), event.getY()));
            }
        });
    }

    public void setBackgrounds() {
        int rows = _rowCount.get();
        int cols = _colCount.get();
        BackgroundFill[] fills = new BackgroundFill[rows * cols + 1];
        double gridWidth = widthProperty().doubleValue();
        double gridHeight = heightProperty().doubleValue();
        Color[] check = {Color.RED, Color.BLUE};
        for (int i = 0; i < cols; i++)
            for (int j = 0; j < rows; j++) {
                Color color = check[(((i % 2) + j) % 2)];
                fills[j + (i * rows)] = new BackgroundFill(color, CornerRadii.EMPTY,
                        new Insets(j * rowHeight(),
                                gridWidth - (colWidth() + colWidth() * i),
                                gridHeight - (rowHeight() + rowHeight() * j),
                                i * colWidth()));
            }
        if (_hoverCoord != null) {
            fills [rows * cols] = new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY,
                    new Insets(_hoverCoord.get().y * rowHeight(),
                    gridWidth - (colWidth() + colWidth() * _hoverCoord.get().x),
                    gridHeight - (rowHeight() + rowHeight() * _hoverCoord.get().y),
                    _hoverCoord.get().x * colWidth()));
        }
        this.setBackground(new Background(fills));
    }

    public int boundCol(double v) {
        return (v < 0) ? 0 : (v >= _colCount.get()) ? _colCount.get() - 1 : (int) v;
    }

    public int boundRow(double v) {
        return (v < 0) ? 0 : (v > _rowCount.get()) ? _rowCount.get() - 1 : (int) v;
    }

    public double colWidth() {
        return widthProperty().doubleValue() / _colCount.get();
    }

    public double rowHeight() {
        return heightProperty().doubleValue() / _rowCount.get();
    }

    public Coord getCoord(double x, double y) {
        return new Coord(boundCol(x / colWidth()), boundRow(y / rowHeight()));
    }

    public final int getColCount() {
        return this._colCount.get();
    }

    public final void setColCount(int var1) {
        this._colCount.set(var1);
    }

    public final int getRowCount() {
        return this._rowCount.get();
    }

    public final void setRowCount(int var1) {
        this._rowCount.set(var1);
    }

}
