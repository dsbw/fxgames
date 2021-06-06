package fxgames;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import javax.swing.*;
import java.util.HashMap;

public class NodeController {

    public static NodeController me;

    public NodeController() {
        me = this;
    }

    public Node node(String name) {
        return nodeMap.get(name);
    }

    private Node active;
    private final HashMap<String, Parent> nodeMap = new HashMap<>();
    protected void addNode(String name, Parent node) {
        nodeMap.put(name, node);
    }

    private final HashMap<Node, EventHandler<ActionEvent>> handlerMap = new HashMap<Node, EventHandler<ActionEvent>>();
    public void addHandler(Node node, EventHandler<ActionEvent> eh) { handlerMap.put(node, eh);}
    public EventHandler<ActionEvent> getHandler(Node node) {return handlerMap.get(node);}

    protected void removeNode(String name) {
        nodeMap.remove(name);
    }

    protected void activate(String name, Pane owner) {
        Node node  = nodeMap.get(name);
        activate(name, owner, event -> {
            owner.getChildren().setAll(node);
        });
    }

    protected void activate(String name, Pane owner, EventHandler<ActionEvent> e) {
        Node node  = nodeMap.get(name);
        node.translateXProperty().set(owner.getWidth());
        owner.getChildren().remove(node);
        owner.getChildren().add(node);

        KeyValue kv = new KeyValue(node.translateXProperty(), 0, Interpolator.EASE_IN);
        KeyFrame kf = new KeyFrame(Duration.seconds(0.25), kv);

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(kf);
        timeline.setOnFinished(e);
        timeline.play();

        active = node;
    }

    public Node getActive() {
        return active;
    }
}