package fxgames;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GridTest {

    private static CountDownLatch startupLatch;
    private static Grid g;
    private static Stage stage;

    public static class TestApp extends Application {
        @Override
        public void start(Stage primaryStage) throws Exception {
            stage = primaryStage;
            g = new Grid(3, 3);
            stage.setScene(new Scene(g));
            System.out.println(g.heightProperty().doubleValue());
            stage.setOnShown(l -> {
                Platform.runLater(() -> startupLatch.countDown());
            });
            stage.show();
        }
    }
    @BeforeClass
    public static void initFX() throws Exception {
        startupLatch = new CountDownLatch(1);
        new Thread(() -> Application.launch(TestApp.class, (String[])null)).start();
        Assertions.assertTrue(startupLatch.await(15, TimeUnit.SECONDS), "Timeout waiting for FX runtime to start");
    }

    @AfterClass
    public static void teardownOnce() {
        Platform.runLater(() -> {
            stage.hide();
            Platform.exit();
        });
    }

    @Test
    public void boundCol() {
        assertEquals(0, g.boundCol(-1), "Column cannot be less than 0.");
        assertEquals(2, g.boundCol(100), "Column should max out at 2.");
    }

    @Test
    public void boundRow() {
        assertEquals(0, g.boundRow(-1), "Row cannot be less than 0.");
        assertEquals(2, g.boundRow(100), "Row should max out at 2.");
    }

    @Test
    public void colWidth() {
        assertEquals(g.widthProperty().doubleValue()/3, g.colWidth(), "Column width should be one third of grid width.");
    }

    @Test
    public void rowHeight() {
        assertEquals(g.heightProperty().doubleValue()/3, g.rowHeight(), "Row height should be one third of grid height.");
    }

    @Test
    public void getCoord() {
        Coord c = g.getCoord(0, 0);
        assertEquals(c.x, 0);
        assertEquals(c.y, 0);
        double h = g.heightProperty().doubleValue();
        double w = g.widthProperty().doubleValue();
        Coord d = g.getCoord(h, w);
        assertEquals(d.x, 2);
        assertEquals(d.y, 2);
    }

}