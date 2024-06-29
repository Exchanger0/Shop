package com.shop.client.elements.views;

import com.shop.client.Starter;
import com.shop.common.RequestResponse;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;

public class ShopProductView extends VBox {
    public ShopProductView(RequestResponse info) {
        setAlignment(Pos.TOP_LEFT);

        ImageView image= new ImageView(new Image(
                new ByteArrayInputStream((byte[]) info.getField(ArrayList.class, "images").getFirst()),
                200, 100, true, true
        ));

        Label name = new Label(info.getField(String.class, "name"));
        name.setFont(new Font(20));
        name.setMaxWidth(120);
        name.setTooltip(new Tooltip(name.getText()));

        Label price = new Label(info.getField(BigDecimal.class, "price") + "$");
        price.setFont(new Font(15));

        getChildren().addAll(image, name, price);
    }
}
