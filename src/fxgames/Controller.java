package fxgames;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.Pane;

public class Controller {

    @FXML
    public Pane central;
    public ButtonBar buttonBar;
    public NodeController nodeController;
    public static Controller me;

    public Controller() {
        me = this;
        nodeController = new NodeController();
        loadNode("tiles-panel", "/resources/main/tilespanel.fxml");
        loadNode("tic-tac-toe", "/resources/ttt/tttgrid.fxml");
        loadNode("dungeon-slippers", "/resources/dunslip/ds-main.fxml");
        loadNode("maze", "/resources/maze/amaze.fxml");
    }

    public void loadNode(String name, String resource) {
        try {
            nodeController.addNode(name, FXMLLoader.load(getClass().getResource(resource)));
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void navigateBackwards(ActionEvent e) {
        if (nodeController.getActive() != nodeController.node("tiles-panel"))
            NodeController.me.activate("tiles-panel", central);
    }

    public void issueCommand(ActionEvent e) {
        EventHandler<ActionEvent> h;
        if ((h = nodeController.getHandler(nodeController.getActive())) != null) {
            h.handle(e);
        }

    }
    public void adjustButtons() {
        var tallest = 0.0;
        for (Node n : buttonBar.getButtons()) {
            var ht = ((Button) n).getHeight();
            if (tallest < ht) tallest = ht;
        }
        for (Node n : buttonBar.getButtons()) {
            ((Button) n).setMinHeight(tallest);
        }
    }
}
