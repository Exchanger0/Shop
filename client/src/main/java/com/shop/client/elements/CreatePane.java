package com.shop.client.elements;

import com.shop.client.Starter;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class CreatePane extends GridPane {
    public CreatePane(Starter starter) {
        setAlignment(Pos.CENTER);
        getChildren().add(new Label("Create"));
    }
}
