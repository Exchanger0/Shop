package com.shop.client.elements;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CartMenu extends VBox {
    public CartMenu() {
        setAlignment(Pos.CENTER);
        getChildren().add(new Label("Cart"));
    }
}
