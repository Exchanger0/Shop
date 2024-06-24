package com.shop.client.elements;


import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class PathPane extends VBox {
    private final VBox content = new VBox();
    public PathPane() {
        setSpacing(10);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setMaxHeight(100);
        content.setSpacing(5);
        content.setFillWidth(true);
        content.setPadding(new Insets(5, 5, 5, 0));
        scrollPane.setContent(content);

        Button add = new Button("+");
        add.setOnAction(e -> {
            TextField field = new TextField();
            field.prefWidthProperty().bind(scrollPane.widthProperty().multiply(0.95));
            content.getChildren().add(field);
        });

        getChildren().addAll(add, scrollPane);
    }

    public List<String> getPaths() {
        List<String> paths = new ArrayList<>();
        for (Node n : content.getChildren()) {
            if (n instanceof TextField t) {
                if (!t.getText().trim().isEmpty()) {
                    paths.add(t.getText());
                }
            }
        }
        return paths;
    }

    public void clear(){
        content.getChildren().clear();
    }
}
