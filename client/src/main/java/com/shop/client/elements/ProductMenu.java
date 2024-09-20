package com.shop.client.elements;

import com.shop.client.Starter;
import com.shop.client.elements.views.ProductView;
import com.shop.common.model.Product;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.List;

public abstract class ProductMenu extends StackPane{
    private final VBox content = new VBox();
    private final ScrollPane scrollContent = new ScrollPane();
    private final StackPane loadPane = new StackPane();
    private final Starter starter;
    public ProductMenu(Starter starter) {
        this.starter = starter;

        scrollContent.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        getChildren().add(scrollContent);

        content.setSpacing(10);
        content.setPadding(new Insets(20));
        content.prefWidthProperty().bind(scrollContent.widthProperty());
        content.setAlignment(Pos.TOP_CENTER);
        scrollContent.setContent(content);
    }

    public void waitLoad() {
        if (loadPane.getChildren().isEmpty()) {
            ProgressIndicator indicator = new ProgressIndicator();
            loadPane.getChildren().add(indicator);
        }
        getChildren().set(0, loadPane);
    }

    public void setProducts(List<Product> products) {
        content.getChildren().clear();
        for (Product pr : products) {
            ProductView pv = getProductView(starter, pr);
            pv.prefWidthProperty().bind(content.widthProperty());
            content.getChildren().addAll(pv, new Separator(Orientation.HORIZONTAL));
        }
        if (products.isEmpty()) {
            Label emptyLabel = new Label("Empty");
            emptyLabel.setFont(new Font(30));
            content.getChildren().add(emptyLabel);
        }
        getChildren().set(0, scrollContent);
    }

    protected abstract ProductView getProductView(Starter starter, Product product);

    public void removeProduct(int id) {
        for (Node n : content.getChildren()) {
            if (n instanceof ProductView cr) {
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
            if (n instanceof ProductView cr) {
                if (cr.getProduct().getId() == id) {
                    cr.updateAmount(newAmount);
                    break;
                }
            }
        }
    }
}
