package fxgames.ttt;

import fxgames.Grid;
import fxgames.Main;
import fxgames.NodeController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;

import java.io.*;

public class TttController {

    @FXML
    public GridPane xobin;
    @FXML
    public transient ImageView X;
    @FXML
    public transient ImageView O;
    @FXML
    public Grid board;
    @FXML
    public Node outerGroup;
    @FXML
    public Group innerGroup;
    @FXML
    public TextField player1;
    @FXML
    public TextField player2;
    @FXML
    public Label p1label;
    @FXML
    public Label p2Label;
    @FXML
    public Label p1Score;
    @FXML
    public Label p2Score;
    @FXML
    public Label tieScore;
    @FXML
    public ImageView XoverO;
    @FXML
    public Label message;

    private Line line = new Line();
    private TicTacToe game = new TicTacToe();
    private TttViewModel tttvm;

    private ChangeListener<Boolean> nameChangeListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            try {
                System.out.println("Calling automove!");
                game.automove();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @FXML
    public void initialize() {

        player1.focusedProperty().addListener(nameChangeListener);
        player2.focusedProperty().addListener(nameChangeListener);

        tttvm = new TttViewModel(board, game, this);
        game.addConsumer((game) -> this.draw());

        NodeController.me.addHandler(outerGroup, (e) -> {
            var id = ((Button) e.getTarget()).getId();
            File file;
            if (id != null) {
                switch (id) {
                    case "new":
                        game.newGame();
                        try {
                            game.automove();
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        break;
                    case "load":
                        file = getFilename(true);
                        if (file != null)
                            try {
                                FileInputStream fileIn = new FileInputStream(file);
                                ObjectInputStream in = new ObjectInputStream(fileIn);
                                game = (TicTacToe) in.readObject();
                                in.close();
                                fileIn.close();
                                player1.setText(game.playerOneName);
                                player2.setText(game.playerTwoName);
                                game.addConsumer((game) -> this.draw());
                                tttvm = new TttViewModel(board, game, this);
                                game.alertConsumers();
                            } catch (IOException | ClassNotFoundException i) {
                                i.printStackTrace();
                                return;
                            }
                        break;
                    case "save":
                        file = getFilename(false);
                        if (file != null) {
                            try {
                                FileOutputStream fileOut = new FileOutputStream(file);
                                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                                out.writeObject(game);
                                out.close();
                                fileOut.close();
                            } catch (IOException i) {
                                i.printStackTrace();
                            }
                        }
                        break;
                    default:
                        System.out.println("what?");
                }
            }
        });
    }

    public void draw() {
        if (!game.winner.equals("")) {
            if (game.winner.equals("-")) {
                message.setText("Game over! " + "It's a tie! Again!");
                return;
            }
            message.setText("Game over! " + game.winner + " wins!");
        }
        p1Score.setText(String.valueOf(game.playerOneWins));
        p2Score.setText(String.valueOf(game.playerTwoWins));
        tieScore.setText(String.valueOf(game.ties));
        if (game.playerOneIsX) {
            XoverO.setRotate(0);
        } else {
            XoverO.setRotate(180);
        }
    }

    public File getFilename(boolean mustExist) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("tic-tac-toe files (*.ttt)", "*.ttt");
        fileChooser.getExtensionFilters().add(extFilter);
        if (mustExist) return fileChooser.showOpenDialog(Main.me.stage);
        else return fileChooser.showSaveDialog(Main.me.stage);
    }

    public void handleOnDragDetected(MouseEvent event) {
        String piece = ((ImageView) event.getSource()).getId();
        if (!game.winner.equals("")) {
            message.setText("The game is over. Press NEW for a new game.");
        } else {
            if (!piece.equals(game.turn)) {
                message.setText("You can't move " + piece + " when it's " + game.turn + "'s turn!");
            } else {
                message.setText(piece + " is being dragged!");
                Dragboard db = X.startDragAndDrop(TransferMode.ANY);

                ClipboardContent content = new ClipboardContent();
                content.putString(piece);
                db.setContent(content);
            }
        }
    }

    public void nameChange(KeyEvent e) {
        String t = (((TextField) e.getTarget()).getText());
        if (e.getTarget() == player1) {
            p1label.setText(t);
            game.playerOneName = t;
        } else {
            p2Label.setText(t);
            game.playerTwoName = t;
        }
    }

    public void togglePlayerX() {
        game.togglePlayerX();
        draw();
    }
}

