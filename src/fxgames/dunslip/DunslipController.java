package fxgames.dunslip;

import fxgames.Coord;
import fxgames.Grid;
import fxgames.NodeController;
import fxgames.dunslip.Dunslip.Thing;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.Arrays;

import static fxgames.dunslip.Dunslip.Direction.*;
import static fxgames.dunslip.Dunslip.GamePiece.wall;

public class DunslipController {

    public ListView<String> complist;
    @FXML
    Grid grid;
    private Dunslip game;
    private DsViewModel dvm;

    @FXML
    public void initialize() {
        System.out.println("DUNSLIP CONTROLLER ACTIVATED");
        NodeController.me.addOnActivate("dungeon-slippers", unused -> grid.requestFocus());

        grid.setColCount(8);
        grid.setRowCount(8);
        grid.setColorPattern(new ArrayList<Color>(Arrays.asList(Color.web("0x717171",1.0), Color.web("0x6F6F6F",1.0))));
        grid.setGridLineThickness(0);
        grid.setBorderThickness(0);
        grid.setBackgroundColor(Color.web("0x7B7B7B"));
        grid.setWallThickness(2);

        game = new Dunslip(10, 10);
        game.player = new Coord(4, 4);
        game.exit = new Coord(2, 2);
        game.add(2,2,  new Thing(wall, DOWN));
        game.add(2, 0, new Thing(wall, LEFT));

        dvm = new DsViewModel(grid, game, this);
        dvm.draw();
        grid.requestFocus();

        complist.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            switch(newValue) {
                case "Wall (left)" -> dvm.thing = new Thing(wall, LEFT);
                case "Wall (right)" -> dvm.thing = new Thing(wall, RIGHT);
                case "Wall (up)" -> dvm.thing = new Thing(wall, UP);
                case "Wall (down)" -> dvm.thing = new Thing(wall, DOWN);
                default -> dvm.thing = null;
            }
        });

    }
}
