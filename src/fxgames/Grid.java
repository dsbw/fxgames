package fxgames;

import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

import static javafx.scene.paint.Color.WHITE;

public class Grid extends StackPane {

    InvalidationListener redraw = observable -> setBackgrounds();

    public Grid() {
        this(2, 2);
    }

    private final SimpleIntegerProperty _colCount = new SimpleIntegerProperty(0, "colCount");
    private final SimpleIntegerProperty _rowCount = new SimpleIntegerProperty(0, "rowCount");
    private Coord hoverCoord = null;
    private final ObservableList<Color> colorPattern;

    private final Pane borderPane = new Pane();
    private final SimpleIntegerProperty _borderThickness = new SimpleIntegerProperty(5);
    private final SimpleObjectProperty<Color> _borderColor = new SimpleObjectProperty<>(Color.BLACK);
    private final SimpleIntegerProperty _gridLineThickness = new SimpleIntegerProperty(2);
    private final SimpleObjectProperty<Color> _gridLineColor = new SimpleObjectProperty<>(Color.BLACK);
    private final SimpleObjectProperty<Color> _hoverColor = new SimpleObjectProperty<>(Color.YELLOW);
    private final SimpleIntegerProperty _wallThickness = new SimpleIntegerProperty(0);
    private final SimpleObjectProperty<Color> _backgroundColor = new SimpleObjectProperty<>(Color.BLUE);

    public final Pane piecePane = new Pane();

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

        piecePane.maxWidthProperty().bind(widthProperty());
        piecePane.maxHeightProperty().bind(heightProperty());
        this.getChildren().add(piecePane);

        this.setOnMouseEntered(event -> setHoverCoord(event.getX(), event.getY()));
        this.setOnMouseExited(event -> clearHoverCoord());
        this.setOnMouseMoved(event -> setHoverCoord(event.getX(), event.getY()));
        this.setOnMouseClicked(event -> {
            setHoverCoord(event.getX(), event.getY());
            if (onGridMouseClicked != null)
                onGridMouseClicked.handle(event, hoverCoord.x, hoverCoord.y);
        });
        this.setOnDragEntered(event -> setHoverCoord(event.getX(), event.getY()));
        this.setOnDragOver(event -> {
            setHoverCoord(event.getX(), event.getY());
            if (onGridDragMove != null)
                if (onGridDragMove.handle(event, hoverCoord.x, hoverCoord.y))
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        });
        this.setOnDragDropped(event -> {
            setHoverCoord(event.getX(), event.getY());
            if (onGridDragDrop != null)
                onGridDragDrop.handle(event, hoverCoord.x, hoverCoord.y);
        });
        this.setOnDragExited(event -> clearHoverCoord());

        colorPattern = FXCollections.observableArrayList(Color.RED, Color.BLUE);
        colorPattern.addListener(redraw);
        _borderThickness.addListener(redraw);
        _borderColor.addListener(redraw);
        _gridLineThickness.addListener(redraw);
        _gridLineColor.addListener(redraw);
        _hoverColor.addListener(redraw);
        _wallThickness.addListener(redraw);
        _backgroundColor.addListener(redraw);
    }

    public GridDragMove onGridDragMove;

    public void setOnGridDragMove(GridDragMove e) {
        this.onGridDragMove = e;
    }

    public GridDragDrop onGridDragDrop;

    public void setOnGridDragDrop(GridDragDrop e) {
        this.onGridDragDrop = e;
    }

    public GridMouseEvent onGridMouseClicked;
    public void setOnGridMouseClicked(GridMouseEvent e) {this.onGridMouseClicked = e;}


    public void setHoverCoord(double x, double y) {
        if (hoverCoord == null) {
            hoverCoord = this.getCoord(x, y);
        } else {
            int nx = getAxisVal(x, true);
            int ny = getAxisVal(y, false);
            if (hoverCoord.x != nx || hoverCoord.y != ny) {
                hoverCoord.x = nx;
                hoverCoord.y = ny;
            }
        }
        setBackgrounds();
    }

    public void clearHoverCoord() {
        hoverCoord = null;
        setBackgrounds();
    }

    public BackgroundFill FillForCoord(int x, int y, Color color) {
        return new BackgroundFill(color, CornerRadii.EMPTY,
                new Insets(y * rowHeight() + _wallThickness.get(),
                        widthProperty().doubleValue() - (colWidth() + colWidth() * x) + _wallThickness.get(),
                        heightProperty().doubleValue() - (rowHeight() + rowHeight() * y) + _wallThickness.get(),
                        x * colWidth() + _wallThickness.get()));
    }

    public void setBackgrounds() {
        int rows = _rowCount.get();
        int cols = _colCount.get();

        Rectangle background = new Rectangle(0, 0, this.widthProperty().doubleValue(), this.heightProperty().doubleValue());

        int numColors = colorPattern.size();
        if (numColors == 0) return;
        background.setFill(WHITE);
        borderPane.getChildren().add(background);

        BackgroundFill[] fills = new BackgroundFill[rows * cols + 2];
        fills[0] = new BackgroundFill(_backgroundColor.get(), CornerRadii.EMPTY, new Insets(0, 0, 0, 0));
        for (int i = 0; i < cols; i++)
            for (int j = 0; j < rows; j++) {
                Color color = colorPattern.get(((i % numColors) + j) % numColors);
                fills[j + (i * rows) + 1] = FillForCoord(i, j, color);
            }
        if (hoverCoord != null) {
            fills[rows * cols + 1] = FillForCoord(hoverCoord.x, hoverCoord.y, _hoverColor.get());
        }
        this.setBackground(new Background(fills));

        borderPane.getChildren().clear();
        Rectangle border = new Rectangle(0, 0, this.widthProperty().doubleValue(), this.heightProperty().doubleValue());
        border.setFill(null);
        border.setStroke(_borderColor.get());
        border.setStrokeWidth(_borderThickness.get());
        borderPane.getChildren().add(border);

        for (int i = 1; i < cols; i++) {
            Line line = new Line(i * colWidth() - 1, 0, i * colWidth() - 1, this.heightProperty().doubleValue());
            line.setStroke(_gridLineColor.get());
            line.setStrokeWidth(_gridLineThickness.get());
            borderPane.getChildren().add(line);
        }

        for (int j = 1; j < rows; j++) {
            Line line = new Line(0, j * rowHeight() - 1, this.widthProperty().doubleValue(), j * rowHeight() - 1);
            line.setStroke(_gridLineColor.get());
            line.setStrokeWidth(_gridLineThickness.get());
            borderPane.getChildren().add(line);
        }
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

    public double wtRowHeight() {
        return heightProperty().doubleValue() / _rowCount.get() - _wallThickness.get();
    }

    public double wtColWidth() {
        return widthProperty().doubleValue() / _colCount.get() - _wallThickness.get();
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

    public final void setColorPattern(ArrayList<Color> cp) {
        colorPattern.clear();
        colorPattern.addAll(cp);
    }

    public final void setBackgroundColor(Color c) {
        _backgroundColor.set(c);
    }

    public final void setBorderColor(Color c) {
        _borderColor.set(c);
    }

    public final void setBorderThickness(int bw) {
        _borderThickness.set(bw);
    }

    public final void setGridLineColor(Color c) {
        _gridLineColor.set(c);
    }

    public final void setGridLineThickness(int glw) {
        _gridLineThickness.set(glw);
    }

    public final int getWallThickness() {
        return _wallThickness.get();
    }

    public final void setWallThickness(int wt) {
        _wallThickness.set(wt);
    }

    public final void setHoverColor(Color c) {
        _hoverColor.set(c);
    }

    public final double cellWidth() {
        return getWidth() / getColCount();
    } //dup of colWidth -- refactor

    public final double cellHeight() {
        return getHeight() / getRowCount();
    } //dup of rowHeight -- refactor

    public final double cellLocalX(int i) {
        return i * cellWidth();
    }

    public final double cellLocalY(int j) {
        return j * cellHeight();
    }

    public Rectangle2D getCellDim(Coord c) {
        return new Rectangle2D(cellLocalX(c.x) + getWallThickness(), cellLocalY(c.y) + getWallThickness(), cellWidth() - getWallThickness() * 2, cellHeight() - getWallThickness() * 2);
    }

    public Rectangle2D getWallDim(Coord c, boolean isHorz, boolean isPlus) {
        double x, y, w, h;
        int wt = _wallThickness.get();
        if (isHorz) {
            y = cellLocalY(isPlus ? c.y + 1 : c.y) - (!isPlus && (c.y == 0) ? 0 : wt);
            x = cellLocalX(c.x);
            w = cellWidth();
            h = ((!isPlus && (c.y == 0)) || ((isPlus && (c.y == _rowCount.get() - 1)))) ? wt : wt * 2;
        } else { //not horizontal
            x = cellLocalX(isPlus ? c.x + 1 : c.x) - (!isPlus && (c.x == 0) ? 0 : wt);
            y = cellLocalY(c.y);
            h = cellHeight();
            w = ((!isPlus && (c.x == 0)) || ((isPlus && (c.x == _colCount.get() - 1)))) ? wt : wt * 2;
        }
        return new Rectangle2D(x, y, w, h);
    }
}
