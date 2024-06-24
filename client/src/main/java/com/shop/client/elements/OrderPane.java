package com.shop.client.elements;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class OrderPane extends VBox {
    public OrderPane() {
        setAlignment(Pos.CENTER);
        getChildren().add(new Label("Orders"));
    }
}
