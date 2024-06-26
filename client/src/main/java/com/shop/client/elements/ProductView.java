package com.shop.client.elements;

import com.shop.client.Starter;
import com.shop.client.model.Product;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.ByteArrayInputStream;


public class ProductView extends GridPane {
    public ProductView(Starter starter, Product product) {
        setPadding(new Insets(10));
        RowConstraints r1 = new RowConstraints();
        r1.setPercentHeight(50);
        RowConstraints r2 = new RowConstraints();
        r2.setPercentHeight(50);
        getRowConstraints().addAll(r1, r2);

        Button back = new Button("<-");
        back.setOnAction(e -> starter.getScene().setRoot(starter.getShopMenu()));

        ScrollPane images = new ScrollPane();
        images.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        HBox content = new HBox();
        images.setContent(content);

        for (byte[] img : product.getPictures()) {
            content.getChildren().add(new ImageView(new Image(
                    new ByteArrayInputStream(img), 300, 200, false, false
            )));
        }
        VBox header = new VBox(back, images);
        header.setSpacing(5);

        ScrollPane info = new ScrollPane();
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.maxWidthProperty().bind(this.widthProperty().subtract(30));
        info.setContent(vBox);

        Label name = new Label(product.getName());
        name.setFont(Font.font(Font.getDefault().getName(), FontWeight.BOLD, 25));
        Label description = new Label(product.getDescription());
        description.setWrapText(true);
        Label amount = new Label("Amount: " + product.getAmount());
        amount.setFont(new Font(20));
        Label price = new Label(product.getPrice() + "$");
        price.setFont(new Font(20));
        vBox.getChildren().addAll(name, description, amount, price);

        add(header, 0 ,0);
        add(info, 0 ,1);
    }
}
