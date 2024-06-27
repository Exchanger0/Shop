package com.shop.client.elements;

import com.shop.client.Starter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.Optional;

public class ProfilePane extends VBox {
    private final Label balance;
    public ProfilePane(Starter starter) {
        setAlignment(Pos.CENTER);
        setSpacing(20);

        Label name = new Label("Username: " + starter.getController().getCurrentUser().getUsername());
        name.setFont(new Font(25));
        balance = new Label("Balance: " + starter.getController().getCurrentUser().getBalance() + "$");
        balance.setFont(new Font(25));

        Button topUp = new Button("Top up balance");
        topUp.setOnAction(e -> {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Top up balance");

            Label l = new Label("Amount:");
            TextField amount = new TextField();

            HBox hBox = new HBox(l ,amount);
            hBox.setAlignment(Pos.CENTER);
            hBox.setPadding(new Insets(10));
            hBox.setSpacing(5);

            dialog.getDialogPane().setContent(hBox);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            Optional<ButtonType> res = dialog.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.OK) {
                try {
                    int am = Integer.parseInt(amount.getText().trim());
                    starter.getController().topUpBalance(am);
                } catch (NumberFormatException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Amount must be number");
                    alert.show();
                }
            }
        });

        getChildren().addAll(name, balance, topUp);
    }

    public void updateBalance(int amount) {
        balance.setText("Balance: " + amount + "$");
    }
}
