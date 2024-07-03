package com.shop.client.elements.views;

import com.shop.client.Starter;
import com.shop.common.model.Product;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.ByteArrayInputStream;

public class ShopProductView extends VBox {
    private BuyProductView buyProductView;
    public ShopProductView(Starter starter, Product product) {
        setAlignment(Pos.TOP_LEFT);
        setOnMousePressed(e -> {
            if (buyProductView == null) {
                buyProductView = new BuyProductView(starter, product);
            }
            starter.getScene().setRoot(buyProductView);
        });

        ImageView image= new ImageView(new Image(
                new ByteArrayInputStream(product.getPictures().getFirst()),
                200, 100, true, true
        ));

        Label name = new Label(product.getName());
        name.setFont(new Font(20));
        name.setMaxWidth(120);
        name.setTooltip(new Tooltip(name.getText()));

        Label price = new Label(product.getPrice() + "$");
        price.setFont(new Font(15));

        getChildren().addAll(image, name, price);
    }
}
