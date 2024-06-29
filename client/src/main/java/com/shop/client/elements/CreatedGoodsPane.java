package com.shop.client.elements;

import com.shop.client.Starter;
import com.shop.client.elements.views.CrProductView;
import com.shop.client.model.Product;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

import java.util.List;

public class CreatedGoodsPane extends VBox {
    private final VBox content = new VBox();
    private final Starter starter;
    public CreatedGoodsPane(Starter starter) {
        this.starter = starter;
        setFillWidth(true);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        getChildren().add(scrollPane);

        content.setSpacing(10);
        content.setPadding(new Insets(20));
        content.prefWidthProperty().bind(scrollPane.widthProperty());
        scrollPane.setContent(content);
    }

    public void setProducts(List<Product> products) {
        content.getChildren().clear();
        for (Product pr : products) {
            CrProductView cr = new CrProductView(starter, pr);
            cr.prefWidthProperty().bind(content.widthProperty());
            content.getChildren().addAll(cr, new Separator(Orientation.HORIZONTAL));
        }
    }

    public void removeProduct(int id) {
        for (Node n : content.getChildren()) {
            if (n instanceof CrProductView cr) {
                if (cr.getProduct().getId() == id) {
                    int sepIndex = content.getChildren().indexOf(n);
                    content.getChildren().remove(n);
                    content.getChildren().remove(sepIndex);
                    break;
                }
            }
        }
    }

    public void updateAmount(int id, int newAmount) {
        for (Node n : content.getChildren()) {
            if (n instanceof CrProductView cr) {
                if (cr.getProduct().getId() == id) {
                    cr.updateAmount(newAmount);
                    break;
                }
            }
        }
    }
}
