package com.shop.client.elements.views;

import com.shop.client.Starter;
import com.shop.client.model.Product;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.StringConverter;

import java.io.ByteArrayInputStream;
import java.util.Optional;


public class CrProductView extends HBox {
    private final Starter starter;
    private final Product product;
    private final Label amount;
    private ProductView productView;
    public CrProductView(Starter starter, Product product) {
        setSpacing(10);
        setOnMousePressed(e -> {
            if (productView == null) {
                productView = new ProductView(starter, product);
            }
            starter.getScene().setRoot(productView);
        });

        this.starter = starter;
        this.product = product;

        ImageView imageView = new ImageView(new Image(
                new ByteArrayInputStream(product.getPictures().getFirst()), 250, 160, true, true
        ));

        Label name = new Label(product.getName());
        name.setFont(new Font(20));

        Label description = new Label(product.getDescription());
        description.setWrapText(true);
        description.setMaxHeight(80);

        Label price = new Label(product.getPrice().toString() + "$");
        price.setFont(new Font(15));

        amount = new Label("Amount: " + product.getAmount());

        VBox content = new VBox(name, description, amount, price);
        content.setSpacing(5);

        Button remove = new Button("⨉");
        remove.setOnAction(e -> showRemoveDialog());

        getChildren().addAll(imageView, content, remove);
    }

    private void showRemoveDialog() {
        Dialog<ButtonType> remove = new Dialog<>();
        remove.setTitle("Remove product");

        Label l = new Label("You want to remove a product: " + product.getName());
        Label l2 = new Label("In quantity:");
        Spinner<Integer> amount = new Spinner<>(0, product.getAmount(), 0);
        amount.setEditable(true);
        amount.getValueFactory().setConverter(new StringConverter<>() {
            @Override
            public String toString(Integer integer) {
                return integer == null ? "0" : String.valueOf(integer);
            }

            @Override
            public Integer fromString(String s) {
                int i;
                try {
                    i = Integer.parseInt(s);
                    if (i > product.getAmount()) {
                        i = 0;
                    }
                } catch (Exception ex) {
                    i = 0;
                }
                return i;
            }
        });

        HBox hBox = new HBox(l2, amount);
        hBox.setSpacing(5);
        hBox.setAlignment(Pos.CENTER);
        VBox vBox = new VBox(l, hBox);
        vBox.setPadding(new Insets(5));
        vBox.setSpacing(10);

        remove.getDialogPane().setContent(vBox);
        remove.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> res = remove.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.YES) {
            if (amount.getValue() != 0) {
                starter.getController().removeProduct(product.getId(), amount.getValue());
            }
        }
    }

    public Product getProduct() {
        return product;
    }

    public void updateAmount(int newAmount) {
        amount.setText("Amount: " + newAmount);
        productView.updateAmount(newAmount);
    }
}
