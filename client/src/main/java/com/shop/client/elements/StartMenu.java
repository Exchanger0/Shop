package com.shop.client.elements;

import com.shop.client.Starter;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class StartMenu extends VBox {
    private RegMenu regMenu;
    private LogInMenu logMenu;

    public StartMenu(Starter starter) {
        Label title = new Label("Shop");
        title.setFont(new Font(60));

        Button regButton = new Button("Registration");
        regButton.setPrefWidth(100);
        regButton.setPrefHeight(30);
        regButton.setOnAction(e -> {
            if (regMenu == null) {
                regMenu = new RegMenu(true, this, starter);
            }
            starter.getScene().setRoot(regMenu);
        });

        Button logButton = new Button("Log in");
        logButton.prefWidthProperty().bind(regButton.widthProperty());
        logButton.prefHeightProperty().bind(regButton.heightProperty());
        logButton.setOnAction(e -> {
            if (logMenu == null) {
                logMenu = new LogInMenu(false, this, starter);
            }
            starter.getScene().setRoot(logMenu);
        });

        setAlignment(Pos.CENTER);
        setSpacing(15);
        getChildren().addAll(title, regButton, logButton);
    }

    public RegMenu getRegMenu() {
        return regMenu;
    }

    public LogInMenu getLogMenu() {
        return logMenu;
    }
}
