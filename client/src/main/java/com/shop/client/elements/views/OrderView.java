package com.shop.client.elements.views;

import com.shop.client.Starter;
import com.shop.common.model.Order;
import com.shop.common.model.Product;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class OrderView extends TitledPane {
    public OrderView(Starter starter, Order order) {
        setText("Order#" + order.getId());
        setExpanded(false);
        setAnimated(false);

        VBox content = new VBox();
        content.setSpacing(5);
        Label price = new Label("Total price: " + order.getTotalPrice() + "$");
        price.setFont(new Font(20));
        content.getChildren().add(price);
        for (Product p : order.getProducts()) {
            Label label = new Label("Name: " + p.getName() + " ; Amount: " + p.getAmount());
            label.setFont(new Font(15));
            label.setOnMouseClicked(e -> {
                if (label.getUserData() == null) {
                    label.setUserData(new FullProductView(starter, p));
                }
                starter.getScene().setRoot((Parent) label.getUserData());
            });
            content.getChildren().add(label);
        }
        setContent(content);
    }
}
