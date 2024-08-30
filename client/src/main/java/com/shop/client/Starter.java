package com.shop.client;

import atlantafx.base.theme.*;
import com.shop.client.elements.*;
import com.shop.common.RequestResponse;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

import static com.shop.common.RequestResponse.Title.SUCCESSFUL_LOG_IN;
import static com.shop.common.RequestResponse.Title.SUCCESSFUL_REGISTRATION;


public class Starter extends Application {
    private final StartMenu startMenu = new StartMenu(this);
    private Scene scene;
    private Controller controller;
    private ShopMenu shopMenu;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        controller = new Controller(this);
    }

    @Override
    public void start(Stage stage) {

        setUserAgentStylesheet(new NordLight().getUserAgentStylesheet());
        scene = new Scene(startMenu);
        stage.setScene(scene);
        stage.setTitle("Shop");
        stage.setHeight(600);
        stage.setWidth(610);
        stage.setOnCloseRequest(e -> controller.exit());
        stage.centerOnScreen();
        stage.show();
    }

    public Scene getScene() {
        return scene;
    }

    public Controller getController() {
        return controller;
    }

    public void registration(RequestResponse response) {
        if (response.getTitle() == SUCCESSFUL_REGISTRATION) {
            controller.logIn(response.getField(String.class, "username"),
                    response.getField(String.class, "password"));
        }else {
            startMenu.getRegMenu().setError("Registration error");
        }
    }

    public void logIn(RequestResponse response) {
        if (response.getTitle() == SUCCESSFUL_LOG_IN) {
            shopMenu = new ShopMenu(this);
            scene.setRoot(shopMenu);
        }else {
            startMenu.getLogMenu().setError("Invalid name and/or password");
        }
    }

    public ShopMenu getShopMenu() {
        return shopMenu;
    }

    public void showErrors(List<String> errors) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setHeaderText("Error");
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox content = new VBox();
        content.setSpacing(5);
        for (String err : errors) {
            content.getChildren().add(new Label(err));
        }
        scrollPane.setContent(content);
        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
    }
}
