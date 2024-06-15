package com.shop.client.elements;

import com.shop.client.Starter;
import com.shop.common.UserType;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public abstract class RegLogMenu extends GridPane {
    protected Label errorLabel = new Label();
    protected Button sendButton = new Button("Send");
    protected ToggleGroup toggleGroup = new ToggleGroup();
    protected TextField usernameField = new TextField();
    protected PasswordField passwordField = new PasswordField();

    public RegLogMenu(boolean isReg, StartMenu parent, Starter starter) {
        setAlignment(Pos.CENTER);
        setHgap(30);
        setVgap(5);

        SimpleBooleanProperty u = new SimpleBooleanProperty(false);
        SimpleBooleanProperty p = new SimpleBooleanProperty(false);

        Label title = new Label(isReg ? "Registration" : "Log In");
        title.setFont(new Font(50));
        GridPane.setColumnSpan(title, 2);
        GridPane.setHalignment(title, HPos.CENTER);
        GridPane.setMargin(title, new Insets(0,0,20,0));

        Label usernameLabel = new Label("Username:");
        usernameLabel.setFont(new Font(20));
        Label errorUsernameLabel = new Label(" ");
        errorUsernameLabel.setMaxHeight(0);
        errorUsernameLabel.setTextFill(Color.RED);
        GridPane.setColumnSpan(errorUsernameLabel, 2);
        usernameField.textProperty().addListener((observable, oldString, newString) -> {
            if (newString.length() < 3){
                u.set(false);
                errorUsernameLabel.setText("Username must contain 3 characters");
            }else {
                u.set(true);
                errorUsernameLabel.setText("");
            }
        });

        Label passwordLabel = new Label("Password:");
        passwordLabel.setFont(new Font(20));
        Label errorPasswordLabel = new Label(" ");
        errorPasswordLabel.setTextFill(Color.RED);
        GridPane.setColumnSpan(errorPasswordLabel, 2);
        passwordField.textProperty().addListener((observable, oldString, newString) -> {
            if (newString.length() < 8){
                p.set(false);
                errorPasswordLabel.setText("Password must contain 8 characters");
            }else {
                p.set(true);
                errorPasswordLabel.setText("");
            }
        });

        errorLabel.setTextFill(Color.RED);
        GridPane.setColumnSpan(errorLabel, 2);
        GridPane.setHalignment(errorLabel, HPos.CENTER);
        GridPane.setMargin(errorLabel, new Insets(5));

        //пока в usernameField или passwordField введены неверные данные кнопка неактивна
        sendButton.disableProperty().bind(u.not().or(p.not()));

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> starter.getScene().setRoot(parent));

        RadioButton consumer = new RadioButton("Consumer");
        consumer.setSelected(true);
        consumer.setToggleGroup(toggleGroup);
        consumer.setUserData(UserType.CONSUMER);
        RadioButton producer = new RadioButton("Producer");
        producer.setToggleGroup(toggleGroup);
        producer.setUserData(UserType.PRODUCER);

        HBox footer = new HBox(consumer, producer, sendButton, backButton);
        footer.setSpacing(10);
        GridPane.setColumnSpan(footer, 2);
        GridPane.setHalignment(footer, HPos.CENTER);

        add(title, 0, 0);
        add(usernameLabel, 0, 1);
        add(usernameField, 1, 1);
        add(errorUsernameLabel, 0, 2);
        add(passwordLabel, 0, 3);
        add(passwordField, 1, 3);
        add(errorPasswordLabel, 0, 4);
        add(footer, 0, 5);
        add(errorLabel, 0, 6);
    }

    public void setError(String message) {
        errorLabel.setText(message);
    }

}
