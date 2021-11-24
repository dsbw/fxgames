package fxgames;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import static fxgames.basicmaze.BasicMaze.Direction.*;
import static fxgames.basicmaze.BasicMaze.Direction.DOWN;

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

        System.out.println("******");
        System.out.println(Controller.me.central);
        System.out.println("******");

        NodeController.me.activate("tiles-panel", Controller.me.central);

        primaryStage.show();
        Controller.me.adjustButtons();
        System.out.println(getClass());
        /*primaryStage.addEventHandler(KeyEvent.ANY, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                System.out.println("App=>"+keyEvent.toString());
            }
        });*/
    }

    public void switchScreen(String name) {
        screenController.activate(name);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
