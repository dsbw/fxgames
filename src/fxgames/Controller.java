package fxgames;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

public class Controller {

    @FXML
    public Pane central;
    public NodeController nodeController;
    public static Controller me;

    public Controller() {
        me = this;
        nodeController = new NodeController();
        loadNode("tiles-panel", "/resources/main/tilespanel.fxml");
        loadNode("tic-tac-toe", "/resources/ttt/tttgrid.fxml");
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
            NodeController.me.activate("tiles-panel", Main.me.bp, event -> {
                Main.me.bp.getChildren().remove(NodeController.me.node("tiles-panel"));
                Main.me.bp.setCenter(NodeController.me.node("tiles-panel"));
            });
    }

    public void issueCommand(ActionEvent e) {
        EventHandler<ActionEvent> h;
        if ((h = nodeController.getHandler(nodeController.getActive())) != null) {
            h.handle(e);
        }
    }
}
