package fxgames.dunslip;

import fxgames.Coord;
import fxgames.Grid;
import fxgames.NodeController;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.Arrays;

public class DunslipController {

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
        game.exit = new Coord(0, 0);

        dvm = new DsViewModel(grid, game, this);
        dvm.draw();
        grid.requestFocus();
    }
}
