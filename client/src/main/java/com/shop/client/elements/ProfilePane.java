package com.shop.client.elements;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ProfilePane extends VBox {
    public ProfilePane() {
        setAlignment(Pos.CENTER);
        getChildren().add(new Label("Profile"));
    }
}
