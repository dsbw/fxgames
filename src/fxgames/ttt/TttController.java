package fxgames.ttt;

import fxgames.Main;
import fxgames.NodeController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
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
    public GridPane board;
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

    private ChangeListener<Boolean> nameChangeListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            try {
                System.out.println("Calling automove!");
                game.automove();
                draw();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @FXML
    public void initialize() {

        player1.focusedProperty().addListener(nameChangeListener);
        player2.focusedProperty().addListener(nameChangeListener);

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
                        draw();
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
                                draw();
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

    public File getFilename(boolean mustExist) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("tic-tac-toe files (*.ttt)", "*.ttt");
        fileChooser.getExtensionFilters().add(extFilter);
        if (mustExist) return fileChooser.showOpenDialog(Main.me.stage);
        else return fileChooser.showSaveDialog(Main.me.stage);
    }

    public void draw() {
        innerGroup.getChildren().remove(line);
        for (int i = board.getChildren().size() - 1; i > 0; i--) board.getChildren().remove(i);

        for (var i = 0; i < 3; i++) {
            for (var j = 0; j < 3; j++)
                if (game.get(i, j) != null) {
                    ImageView xo = new ImageView();

                    if (game.get(i, j).equals("X"))
                        xo.setImage(X.getImage());
                    else if (game.get(i, j).equals("O"))
                        xo.setImage(O.getImage());
                    xo.setFitWidth(Math.round(board.getWidth() / 3));
                    xo.setFitHeight(Math.round(board.getHeight() / 3));
                    GridPane.setRowIndex(xo, i);
                    GridPane.setColumnIndex(xo, j);
                    board.getChildren().addAll(xo);
                }
        }
        drawVictorySlash();

        p1Score.setText(String.valueOf(game.playerOneWins));
        p2Score.setText(String.valueOf(game.playerTwoWins));
        tieScore.setText(String.valueOf(game.ties));
        if (game.playerOneIsX) {
            XoverO.setRotate(0);
        } else {
            XoverO.setRotate(180);
        }
    }

    public void drawVictorySlash() {
        if (!game.winner.equals("")) {
            if (game.winner.equals("-")) {
                message.setText("Game over! " + "It's a tie! Again!");
                return;
            }
            message.setText("Game over! " + game.winner + " wins!");
            var bw = board.getWidth();
            var bh = board.getHeight();
            var cw = bw / 3;
            var ch = bh / 3;
            var wxs = 0.0;
            var wys = 0.0;
            var wxe = bw;
            var wye = bh;

            innerGroup.getChildren().add(line);
            line.getStyleClass().add("line");
            if (game.vxd == 1 && game.vyd == 1) {
                //don't actually have to do anything since these are the defaults, but lets leave the case in here
            } else if (game.vxd == -1 && game.vyd == 1) {
                wys = bh;
                wye = 0;
            } else if (game.vxd == 1) {
                wys = (game.vy * ch) + (ch / 2);
                wye = wys;
            } else if (game.vyd == 1) {
                wxs = (game.vx * cw) + (cw / 2);
                wxe = wxs;
            }
            line.setStartX(wxs);
            line.setStartY(wys);
            line.setEndX(wxe);
            line.setEndY(wye);
        }
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

    public int getCoord(double width, double coord, int numberOfSections) {
        long dim = Math.round(width / numberOfSections);
        long border = dim;
        int val = 0;
        while (coord > border) {
            border += dim;
            val++;
        }
        return val;
    }

    public void handleOnDragOver(DragEvent event) {
        int column = getCoord(board.getWidth(), event.getX(), board.getRowCount());
        int row = getCoord(board.getHeight(), event.getY(), board.getColumnCount());

        if ((column >= 0 && column <= 2) &&
                (row >= 0 && row <= 2) &&
                (game.get(row, column) == null)) {
            if (event.getGestureSource() != board && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
        }
        event.consume();
    }

    public void handleOnDrop(DragEvent event) {
        int row = getCoord(board.getWidth(), event.getX(), board.getRowCount());
        int column = getCoord(board.getHeight(), event.getY(), board.getColumnCount());

        try {
            game.addPiece(event.getDragboard().getString(), column, row);
        } catch (Exception e) {
            e.printStackTrace();
        }
        draw();
    }

    public boolean handler(ActionEvent e) {
        System.out.println(e);
        return false;
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

