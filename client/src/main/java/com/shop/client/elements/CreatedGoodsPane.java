package com.shop.client.elements;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CreatedGoodsPane extends VBox {
    public CreatedGoodsPane() {
        setAlignment(Pos.CENTER);
        getChildren().add(new Label("Create goods"));
    }
}
