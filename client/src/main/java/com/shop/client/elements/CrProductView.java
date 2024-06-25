package com.shop.client.elements;

import com.shop.client.model.Product;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.ByteArrayInputStream;


public class CrProductView extends HBox {
    public CrProductView(Product product) {
        setSpacing(10);
        setPadding(new Insets(5));
        setBorder(new Border(new BorderStroke(Color.rgb(71, 98, 131), BorderStrokeStyle.SOLID, null, new BorderWidths(1))));

        ImageView imageView = new ImageView(new Image(
                new ByteArrayInputStream(product.getPictures().getFirst()), 250, 150, true, true
        ));

        Label name = new Label(product.getName());
        name.setFont(new Font(20));

        Label description = new Label(product.getDescription());
        description.setWrapText(true);
        description.setMaxHeight(80);

        Label price = new Label(product.getPrice().toString());
        price.setFont(new Font(15));

        VBox content = new VBox(name, description, price);
        content.setSpacing(5);

        getChildren().addAll(imageView, content);
    }
}
