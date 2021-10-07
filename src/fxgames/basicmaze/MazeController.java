package fxgames.basicmaze;

import fxgames.Grid;
import fxgames.Main;
import fxgames.NodeController;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class MazeController {

    @FXML
    Grid grid;

    private MazeViewModel mvm;
    BasicMaze maze = new BasicMaze(7, 5);

    /* Lifted from TttController: Refactor and generalize */
    public File getFilename(boolean mustExist) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("maze files (*.maz)", "*.maz");
        fileChooser.getExtensionFilters().add(extFilter);
        if (mustExist) return fileChooser.showOpenDialog(Main.me.stage);
        else return fileChooser.showSaveDialog(Main.me.stage);
    }

    @FXML
    public void initialize() {
        System.out.println("MAZE CONTROLLER ACTIVATED");
        NodeController.me.addOnActivate("maze", unused -> grid.requestFocus());
        grid.setColorPattern(new ArrayList<Color>(Arrays.asList(Color.web("0x777777",1.0), Color.web("0x7F7F7F",1.0))));
        grid.setGridLineThickness(0);
        grid.setBorderThickness(0);
        grid.setBackgroundColor(Color.web("0x7B7B7B"));
        grid.setWallThickness(2);

        NodeController.me.addHandler(grid, (e) -> {
            var id = ((Button) e.getTarget()).getId();
            File file;
            if (id != null) {
                switch (id) {
                    case "new" -> {
                        Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL);

                        GridPane layout = new GridPane();
                        layout.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));
                        layout.setVgap(5);
                        layout.setHgap(5);
                        Label prompt = new Label("Set the dimensions:");
                        Label pwidth = new Label("Width:");
                        Label pheight = new Label("Height");

                        Spinner<Integer> width = new Spinner<Integer>(2, 250, maze.getWidth());
                        width.editableProperty().set(true);
                        Spinner<Integer> height = new Spinner<Integer>(2, 250, maze.getHeight());
                        height.editableProperty().set(true);

                        Button button = new Button("Generate!");
                        button.setOnAction(ev -> {
                            stage.close();
                            if (mvm!=null) maze.removeConsumer(mvm.drawFn);
                            else mvm = new MazeViewModel(grid, maze, this);
                            maze.reset(width.getValue(), height.getValue());
                            regenerate();
                        });

                        Button button2 = new Button("Never mind");
                        button2.setOnAction(ev -> {
                            stage.close();
                        });

                        layout.add(prompt, 1,1);
                        layout.add(pwidth, 1, 2);
                        layout.add(width, 2, 2);
                        layout.add(pheight, 1, 3);
                        layout.add(height, 2, 3);
                        layout.add(button, 1,4);
                        layout.add(button2, 2, 4);
                        Scene scene = new Scene(layout, 250, 150);
                        stage.setTitle("Regenerate the maze?");
                        stage.setScene(scene);
                        stage.showAndWait();

                        maze.assignCoords();
                        mvm.draw();
                        grid.requestFocus();
                    }
                    case "load" -> {
                        file = getFilename(true);
                        if (file != null)
                            try {
                                FileInputStream fileIn = new FileInputStream(file);
                                ObjectInputStream in = new ObjectInputStream(fileIn);
                                maze = (BasicMaze) in.readObject();
                                in.close();
                                fileIn.close();
                                mvm = new MazeViewModel(grid, maze, this);
                            } catch (IOException | ClassNotFoundException i) {
                                i.printStackTrace();
                            }
                        mvm.draw();
                    }
                    case "save" -> {
                        file = getFilename(false);
                        if (file != null) {
                            try {
                                FileOutputStream fileOut = new FileOutputStream(file);
                                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                                out.writeObject(maze);
                                out.close();
                                fileOut.close();
                            } catch (IOException i) {
                                i.printStackTrace();
                            }
                        }
                    }
                    default -> System.out.println("what?");
                }
            }
        });
    }

    public void regenerate() {
        mvm.calls = 0;
        mvm.draw();
        new Thread(() -> {
            maze.generate();
        }).start();
    }
}
