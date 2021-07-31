package fxgames.dunslip;

import fxgames.Grid;
import javafx.fxml.FXML;

public class DunslipController {

    @FXML
    Grid grid1;

    @FXML
    public void initialize() {
        System.out.println("DUNSLIP CONTROLLER ACTIVATED");
    }

    @FXML
    public void randomize() {
        /*int x = (int) (Math.random()*200+1);
        int y = (int) (Math.random()*200+1);
        System.out.printf("Setting grid to %d,%d\n", x, y);
        grid1.setColCount(x);
        grid1.setRowCount(y);*/
    }
}
