package com.shop.client.elements;

import com.shop.client.Starter;
import com.shop.client.elements.views.ShopProductView;
import com.shop.common.model.Product;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.util.List;

public class ProductPane extends BorderPane {
    private final FlowPane content = new FlowPane();
    private final ScrollPane scrollContent;
    private final StackPane loadPane = new StackPane();
    private final Button previous = new Button("Previous");
    private final Button next = new Button("Next");
    private final Starter starter;
    public ProductPane(Starter starter) {
        this.starter = starter;
        content.setAlignment(Pos.TOP_CENTER);
        content.setHgap(5);
        content.setVgap(5);

        scrollContent = new ScrollPane(content);
        scrollContent.viewportBoundsProperty().addListener((bounds, oldBounds, newBounds) ->
                content.setPrefWidth(newBounds.getWidth()));

        previous.setOnAction(e -> {
            previous.setDisable(true);
            next.setDisable(true);
            waitLoad();
            starter.getController().getPreviousProducts();
        });
        next.setOnAction(e -> {
            previous.setDisable(true);
            next.setDisable(true);
            waitLoad();
            starter.getController().getNextProducts();
            scrollContent.setVvalue(0);
        });

        HBox buttonBar = new HBox(previous, next);
        buttonBar.setSpacing(10);
        buttonBar.setPadding(new Insets(10));
        buttonBar.setAlignment(Pos.CENTER);

        setCenter(scrollContent);
        setBottom(buttonBar);
    }

    public void waitLoad() {
        if (loadPane.getChildren().isEmpty()) {
            ProgressIndicator indicator = new ProgressIndicator();
            loadPane.getChildren().add(indicator);
        }
        setCenter(loadPane);
    }

    public void setProducts(List<Product> products) {
        if (!products.isEmpty()) {
            content.getChildren().clear();
            for (Product pr : products) {
                content.getChildren().add(new ShopProductView(starter, pr));
            }
            Pane pane = new Pane();
            pane.setMinHeight(60);
            pane.setMinWidth(10);
            content.getChildren().add(pane);
            setCenter(scrollContent);
        }
        resetButtons();
    }

    public void resetButtons() {
        previous.setDisable(false);
        next.setDisable(false);
    }
}
