package fxgames.basicmaze;

import fxgames.Grid;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;

public class MazeController {

    @FXML
    Grid grid;

    @FXML
    public void initialize() {
        System.out.println("MAZE CONTROLLER ACTIVATED");
        grid.setColorPattern(new ArrayList<Color>(Arrays.asList(Color.web("0x777777",1.0), Color.web("0x7F7F7F",1.0))));
        grid.setGridLineThickness(0);
        grid.setBorderThickness(0);

        BasicMaze maze = new BasicMaze(7, 5);
        maze.generate();
        maze.print();
    }

}
