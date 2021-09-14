package fxgames.basicmaze;

import fxgames.Grid;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;

public class MazeController {

    @FXML
    Grid grid;

    private MazeViewModel mvm;
    BasicMaze maze = new BasicMaze(7, 5);

    @FXML
    public void initialize() {
        System.out.println("MAZE CONTROLLER ACTIVATED");
        grid.setColorPattern(new ArrayList<Color>(Arrays.asList(Color.web("0x777777",1.0), Color.web("0x7F7F7F",1.0))));
        grid.setGridLineThickness(0);
        grid.setBorderThickness(0);
        grid.setBackgroundColor(Color.web("0x7B7B7B"));
        grid.setWallThickness(2);

        mvm = new MazeViewModel(grid, maze, this);
    }

    @FXML
    public void randomize() {
        //maze.reset((int) (Math.random() * 40 + 40), (int) (Math.random() * 40 + 25));
        maze.reset(120, 75);
        mvm.calls = 0;
        mvm.draw();
        new Thread(() -> {
            maze.generate();
        }).start();
    }
}
