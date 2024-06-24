package com.shop.client.elements;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

public class ProductPane extends FlowPane {
    public ProductPane() {
        setAlignment(Pos.CENTER);
        getChildren().add(new Label("Products"));
    }
}
