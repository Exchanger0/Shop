package com.shop.client.elements;

import com.shop.client.Starter;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CreatePane extends GridPane {
    public CreatePane(Starter starter) {
        setAlignment(Pos.CENTER);
        setPadding(new Insets(5));
        setHgap(15);
        setVgap(10);
        ColumnConstraints c = new ColumnConstraints();
        c.setPercentWidth(20);
        getColumnConstraints().add(c);

        Label nameL = new Label("Name:");
        TextField name = new TextField();

        Label descrL = new Label("Description:");
        TextArea descr = new TextArea();
        descr.setWrapText(true);

        Label priceL = new Label("Price:");
        TextField price = new TextField();

        Label amountL = new Label("Amount:");
        TextField amount = new TextField();

        Label imgL = new Label("Images:");
        PathPane pathPane = new PathPane();

        Button create  = new Button("Create");
        create.disableProperty().bind(descrL.textProperty().isEmpty());
        setColumnSpan(create, 2);
        setHalignment(create, HPos.CENTER);
        create.setOnAction(e -> {
            String nameS = name.getText().trim();
            if (nameS.isEmpty()) {
                showError("Name must not be empty");
                return;
            }
            BigDecimal pr;
            try {
                pr = BigDecimal.valueOf(Long.parseLong(price.getText()));
            }catch (NumberFormatException ex) {
                showError("Price must be number");
                return;
            }

            if (pathPane.getPaths().isEmpty()) {
                showError("There must be at least one image");
                return;
            }
            int am;
            try {
                am = Integer.parseInt(amount.getText().trim());
                if (am < 0) {
                    showError("Amount must be greater than zero");
                    return;
                }
            }catch (NumberFormatException ex) {
                showError("Amount must be number");
                return;
            }
            List<byte[]> images = new ArrayList<>();
            for (String str : pathPane.getPaths()) {
                try {
                    if (str.endsWith("png") || str.endsWith("jpg") || str.endsWith("jpeg")) {
                        images.add(Files.readAllBytes(Paths.get(str)));
                    }else {
                        showError("Only png, jpg, jpeg images are supported");
                        return;
                    }
                } catch (IOException ex) {
                    showError("Wrong image path");
                    return;
                }
            }
            starter.getController().createProduct(name.getText(), descr.getText(), pr, am, images);
            name.setText("");
            descr.setText("");
            price.setText("");
            amount.setText("");
            pathPane.clear();
        });

        add(nameL, 0, 0);
        add(name, 1, 0);
        add(descrL, 0, 1);
        add(descr, 1, 1);
        add(priceL, 0, 2);
        add(price, 1, 2);
        add(amountL, 0, 3);
        add(amount, 1, 3);
        add(imgL, 0, 4);
        add(pathPane, 1, 4);
        add(create, 0, 5);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(message);
        alert.show();
    }
}
