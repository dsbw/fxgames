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
import static fxgames.dunslip.Dunslip.GamePiece.*;
import static fxgames.dunslip.Dunslip.getID;

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
        game.add(new Thing(getID(), PLAYER, 4, 4, null, true, false, false, false, false, false, null));
        game.exit = new Coord(2, 2);
        game.add(Dunslip.Wall(8, 8, RIGHT));
        game.add(Dunslip.Wall(1, 0, LEFT));
        game.add(new Thing(getID(), TREASURE, 4, 8, null, true, true, false, false, false, false, null));
        game.add(Dunslip.Evil_Eye(4, 9, UP));
        game.add(new Thing(getID(), GOBLIN, 8, 7, null, true, true, true, false, false, false, null));
        //game.add(new Thing(getID(), PIT, 8, 1, null, false, false, false, true, false));
        game.add(new Thing(getID(), HOBGOBLIN, 0, 9, RIGHT, true, true, false, false, true, false, null));


        dvm = new DsViewModel(grid, game, this);
        dvm.draw();
        grid.requestFocus();

        complist.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            switch(newValue) {
                case "Wall (left)" -> dvm.thing = Dunslip.Wall(-1, -1, LEFT);
                case "Wall (right)" -> dvm.thing = Dunslip.Wall(-1, -1, RIGHT);
                case "Wall (up)" -> dvm.thing = Dunslip.Wall(-1, -1, UP);
                case "Wall (down)" -> dvm.thing = Dunslip.Wall(-1, -1, DOWN);
                case "Treasure" -> dvm.thing = new Thing(getID(), TREASURE, -1, -1, null, true, true, false, false, false, false, null);
                case "Goblin" -> dvm.thing = new Thing(getID(), GOBLIN, -1, -1, null, true, true, true, false, false, false, null);
                case "Hobgoblin (left)" -> dvm.thing = Dunslip.Hobgoblin(-1, -1, LEFT);
                case "Hobgoblin (right)" -> dvm.thing = Dunslip.Hobgoblin(-1, -1, RIGHT);
                case "Hobgoblin (up)" -> dvm.thing = Dunslip.Hobgoblin(-1, -1, UP);
                case "Hobgoblin (down)" -> dvm.thing = Dunslip.Hobgoblin(-1, -1, DOWN);
                default -> dvm.thing = null;
            }
        });

    }
}
