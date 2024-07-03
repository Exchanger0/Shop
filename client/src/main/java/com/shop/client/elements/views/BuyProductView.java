package com.shop.client.elements.views;

import com.shop.client.Starter;
import com.shop.common.model.Product;
import javafx.scene.control.Button;

public class BuyProductView extends FullProductView {
    public BuyProductView(Starter starter, Product product) {
        super(starter, product);

        Button add = new Button("Add to cart");
        add.setOnAction(e -> starter.getController().addToCart(product.getId()));
        contentInfo.getChildren().add(add);
    }
}
