package fxgames;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    public static Main me;
    public Stage stage;
    public ScreenController screenController; //For replacing EVERYTHING
    public NodeController nodeController; //For just replacing nodes (primarily in the central pane)
    public BorderPane bp;
    public ButtonBar bb;

    @Override
    public void start(Stage primaryStage) throws Exception{
        me = this;
        stage = primaryStage;
        primaryStage.setTitle("Fun 'n' Games with JavaFX");
        Scene scene = new Scene(new Group(), 800, 600);
        scene.getStylesheets().add(getClass().getResource("../resources/css/styles.css").toExternalForm());
        primaryStage.setScene(scene);

        screenController = new ScreenController(scene); //not using, currently
        screenController.addScreen("frame", FXMLLoader.load(getClass().getResource("../resources/main/frame.fxml")));
        switchScreen("frame");
        bp = ((BorderPane) scene.getRoot());
        bb = (ButtonBar) bp.getTop();

        NodeController.me.activate("tiles-panel", bp, event -> {
            bp.getChildren().remove(NodeController.me.node("tiles-panel"));
            bp.setCenter(NodeController.me.node("tiles-panel"));
        });
        primaryStage.show();
    }

    public void switchScreen(String name) {
        screenController.activate(name);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
