package com.shop.client;

import com.shop.client.elements.StartMenu;
import com.shop.common.RequestResponse;
import com.shop.common.UserType;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import static com.shop.common.RequestResponse.Title.SUCCESSFUL_LOG_IN;
import static com.shop.common.RequestResponse.Title.SUCCESSFUL_REGISTRATION;


public class Starter extends Application {
    private final StartMenu startMenu = new StartMenu(this);
    private Scene scene;
    private Controller controller;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        controller = new Controller(this);
    }

    @Override
    public void start(Stage stage) throws Exception {

        scene = new Scene(startMenu);
        stage.setScene(scene);
        stage.setTitle("Shop");
        stage.setHeight(600);
        stage.setWidth(600);
        stage.setOnCloseRequest(e -> {
            controller.exit();
        });
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
            controller.logIn(response.getField(UserType.class, "user_type"),
                    response.getField(String.class, "username"),
                    response.getField(String.class, "password"));
        }else {
            startMenu.getRegMenu().setError("Registration error");
        }
    }

    public void logIn(RequestResponse response) {
        if (response.getTitle() == SUCCESSFUL_LOG_IN) {
            if (response.getField(UserType.class, "user_type").equals(UserType.CONSUMER)) {
                scene.setRoot(new StackPane(new Label("Consumer")));
            }else {
                scene.setRoot(new StackPane(new Label("Producer")));
            }
        }else {
            startMenu.getLogMenu().setError("Invalid name and/or password");
        }
    }


}
