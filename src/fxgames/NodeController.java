package fxgames;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.function.Consumer;
import java.util.HashMap;
import java.util.Random;

import static fxgames.basicmaze.BasicMaze.Direction.*;
import static fxgames.basicmaze.BasicMaze.Direction.DOWN;

public class NodeController {

    public static NodeController me;
    public Random rand = new Random();

    public NodeController() {
        me = this;
    }

    public Node node(String name) {
        return nodeMap.get(name);
    }

    private Node active;
    private final HashMap<String, Parent> nodeMap = new HashMap<>();
    private final HashMap<String, Consumer<Void>> activateMap = new HashMap<>();

    protected void addNode(String name, Parent node) {
        nodeMap.put(name, node);
    }
    public void addOnActivate(String name, Consumer<Void> c) {
        activateMap.put(name, c);
    }

    private final HashMap<Node, EventHandler<ActionEvent>> handlerMap = new HashMap<Node, EventHandler<ActionEvent>>();

    public void addHandler(Node node, EventHandler<ActionEvent> eh) {
        handlerMap.put(node, eh);
    }

    public EventHandler<ActionEvent> getHandler(Node node) {
        return handlerMap.get(node);
    }

    protected void removeNode(String name) {
        nodeMap.remove(name);
    }

    protected void activate(String name, Pane owner) {
        Node node = nodeMap.get(name);
        activate(name, owner, event -> {
            var outro = owner.getChildren().get(0);
            double x = outro.getLayoutX();
            double o = outro.getOpacity();
            owner.getChildren().removeAll();
            owner.getChildren().setAll(node);
            outro.setLayoutX(x);
            outro.setOpacity(o);
            Consumer<Void> c = activateMap.get(name);
            if (c!=null) c.accept(null);
        });
        if (owner instanceof javafx.scene.layout.AnchorPane) {
            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);
        }
    }

    protected void activate(String name, Pane owner, EventHandler<ActionEvent> e) {
        Node node = nodeMap.get(name);
        node.opacityProperty().set(100);
        node.translateXProperty().set(0);
        owner.getChildren().remove(node);
        owner.getChildren().add(node);

        int transitionType = rand.nextInt(2);

        switch (transitionType) {
            case 0 -> { //refactor to a single timeline?
                node.translateXProperty().set(owner.getWidth());
                KeyValue kv = new KeyValue(node.translateXProperty(), 0, Interpolator.EASE_IN);
                KeyFrame kf = new KeyFrame(Duration.seconds(0.25), kv);
                Timeline timeline = new Timeline();
                timeline.getKeyFrames().add(kf);
                timeline.setOnFinished(e);

                var outro = owner.getChildren().get(0);

                if (outro == node) {
                    timeline.play();
                    return;
                }

                KeyValue kv2 = new KeyValue(outro.translateXProperty(), -owner.getWidth(), Interpolator.EASE_IN);
                KeyFrame kf2 = new KeyFrame(Duration.seconds(0.25), kv2);
                Timeline timeline2 = new Timeline();
                timeline2.getKeyFrames().add(kf2);

                var parallelTransition = new ParallelTransition();
                parallelTransition.getChildren().addAll(
                        timeline,
                        timeline2
                );
                parallelTransition.play();
            }
            case 1 -> {
                FadeTransition trans = new FadeTransition(Duration.seconds(0.5), node);
                trans.setFromValue(0.0);
                trans.setToValue(1.0);
                trans.setOnFinished(e);
                var outro = owner.getChildren().get(0);

                if (outro == node) {
                    trans.play();
                    return;
                }

                var trans2 = new FadeTransition(Duration.seconds(0.5), outro);
                trans2.setFromValue(1.0);
                trans2.setToValue(0.0);

                var parallelTransition = new ParallelTransition();
                parallelTransition.getChildren().addAll(
                        trans,
                        trans2
                );
                parallelTransition.play();
            }
        }
        active = node;
    }

    public Node getActive() {
        return active;
    }
}