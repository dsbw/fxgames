package fxgames;

import com.sun.javafx.binding.BidirectionalBinding;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import javax.naming.Binding;

public class Grid extends StackPane {

    InvalidationListener redraw = observable -> setBackgrounds();

    public Grid() {
        this(2, 2);
    }

    private final SimpleIntegerProperty _colCount = new SimpleIntegerProperty(0, "colCount");
    private final SimpleIntegerProperty _rowCount = new SimpleIntegerProperty(0, "rowCount");
    private Coord hoverCoord = null;

    private final Pane borderPane = new Pane();
    private Rectangle border = null;

    public Grid(int columns, int rows) {
        setColCount(columns);
        setRowCount(rows);

        widthProperty().addListener(redraw);
        heightProperty().addListener(redraw);
        _colCount.addListener(redraw);
        _rowCount.addListener(redraw);

        borderPane.maxWidthProperty().bind(widthProperty());
        borderPane.maxHeightProperty().bind(heightProperty());
        this.getChildren().add(borderPane);

        this.setOnMouseEntered(event -> {
            hoverCoord = this.getCoord(event.getX(), event.getX());
            setBackgrounds();
        });

        this.setOnMouseExited(event -> {
            hoverCoord = null;
            setBackgrounds();
        });

        this.setOnMouseMoved(event -> {
            int x = getAxisVal(event.getX(), true);
            int y = getAxisVal(event.getY(), false);
            if (hoverCoord.x != x || hoverCoord.y != y) {
                this.setCoord(hoverCoord, event.getX(), event.getY());
                this.setBackgrounds();
            }
        });
    }

    public BackgroundFill FillForCoord(int x, int y, Color color) {
        return new BackgroundFill(color, CornerRadii.EMPTY,
                new Insets(y * rowHeight(),
                        widthProperty().doubleValue() - (colWidth() + colWidth() * x),
                        heightProperty().doubleValue() - (rowHeight() + rowHeight() * y),
                        x * colWidth()));
    }

    public void setBackgrounds() {
        int rows = _rowCount.get();
        int cols = _colCount.get();
        BackgroundFill[] fills = new BackgroundFill[rows * cols + 1];
        Color[] check = {Color.RED, Color.BLUE};
        for (int i = 0; i < cols; i++)
            for (int j = 0; j < rows; j++) {
                Color color = check[(((i % 2) + j) % 2)];
                fills[j + (i * rows)] = FillForCoord(i, j, color);
            }
        if (hoverCoord != null) {
            fills[rows * cols] = FillForCoord(hoverCoord.x, hoverCoord.y, Color.YELLOW);
        }
        this.setBackground(new Background(fills));

        borderPane.getChildren().remove(border);
        border = new Rectangle(0, 0, this.widthProperty().doubleValue(), this.heightProperty().doubleValue());
        border.setFill(null);
        border.setStroke(Color.BLACK);
        border.setStrokeWidth(5);
        borderPane.getChildren().add(border);
    }

    public int boundCol(double v) {
        return (v < 0) ? 0 : (v >= _colCount.getValue()) ? _colCount.getValue() - 1 : (int) v;
    }

    public int boundRow(double v) {
        return (v < 0) ? 0 : (v > _rowCount.getValue()) ? _rowCount.getValue() - 1 : (int) v;
    }

    public double colWidth() {
        return widthProperty().doubleValue() / _colCount.get();
    }

    public double rowHeight() {
        return heightProperty().doubleValue() / _rowCount.get();
    }

    public int getAxisVal(double w, boolean isX) {
        return isX ? boundCol(w / colWidth()) : boundRow(w / rowHeight());
    }

    public Coord getCoord(double x, double y) {
        return new Coord(getAxisVal(x, true), getAxisVal(y, false));
    }

    public void setCoord(Coord c, double x, double y) {
        c.x = getAxisVal(x, true);
        c.y = getAxisVal(y, false);
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
