package fxgames.dunslip;

import fxgames.Grid;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DunslipController {

    @FXML
    Grid grid1;

    @FXML
    public void initialize() {
        System.out.println("DUNSLIP CONTROLLER ACTIVATED");
    }

    @FXML
    public void randomize() {
        int x = (int) (Math.random()*40+2);
        int y = (int) (Math.random()*40+2);
        System.out.printf("Setting grid to %d,%d\n", x, y);
        grid1.setColCount(x);
        grid1.setRowCount(y);

        ArrayList<Color> cp = new ArrayList<Color>();

        System.out.print("Colors: ");
        for(int i = 0; i < Math.random()*10; i++) {
            Color c = new Color(Math.random(), Math.random(), Math.random(), 1);
            System.out.print(" - "+c.toString());
            cp.add(c);
        }
        System.out.printf(" => %d\n", cp.size() );
        grid1.setColorPattern(cp);

        int bw = (int)Math.round(Math.random()*20);
        System.out.printf("Border width to =>%d\n", bw);
        grid1.setBorderThickness(bw);
        grid1.setBorderColor(new Color(Math.random(), Math.random(), Math.random(), 1));
        int glw = (int)Math.round(Math.random()*3)*2;
        System.out.printf("Grid line width to =>%d\n", glw);
        grid1.setGridLineThickness(glw);
        grid1.setGridLineColor(new Color(Math.random(), Math.random(), Math.random(), 1));
        grid1.setHoverColor(new Color(Math.random(), Math.random(), Math.random(), 1));
    }
}
