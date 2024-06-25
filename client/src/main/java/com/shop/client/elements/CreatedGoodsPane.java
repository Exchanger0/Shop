package com.shop.client.elements;

import com.shop.client.model.Product;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

import java.util.List;

public class CreatedGoodsPane extends VBox {
    private final VBox content = new VBox();
    public CreatedGoodsPane() {
        setFillWidth(true);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        getChildren().add(scrollPane);

        content.setSpacing(10);
        content.setPadding(new Insets(20));
        content.prefWidthProperty().bind(scrollPane.widthProperty());
        scrollPane.setContent(content);
    }

    public void addProduct(Product product) {
        content.getChildren().addAll(new CrProductView(product), new Separator(Orientation.HORIZONTAL));
    }

    public void addProducts(List<Product> products) {
        for (Product pr : products) {
            content.getChildren().addAll(new CrProductView(pr), new Separator(Orientation.HORIZONTAL));
        }
    }
}
