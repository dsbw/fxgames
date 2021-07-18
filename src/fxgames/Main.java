package fxgames;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static Main me;
    public Stage stage;
    public ScreenController screenController; //For replacing EVERYTHING

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

        NodeController.me.activate("tiles-panel", Controller.me.central);

        primaryStage.show();
    }

    public void switchScreen(String name) {
        screenController.activate(name);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
