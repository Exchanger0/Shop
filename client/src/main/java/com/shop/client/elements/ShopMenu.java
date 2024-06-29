package com.shop.client.elements;

import com.shop.client.Starter;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.Objects;

public class ShopMenu extends BorderPane {
    private CreatePane createPane;
    private CreatedGoodsPane createdGoodsPane;
    private ProfilePane profilePane;
    private ProductPane productPane;

    public ShopMenu(Starter starter) {
        GridPane buttonPane = new GridPane();
        ColumnConstraints r = new ColumnConstraints();
        r.setHgrow(Priority.ALWAYS);
        r.setFillWidth(true);
        buttonPane.getColumnConstraints().addAll(r,r,r,r,r,r);

        ToggleGroup group = new ToggleGroup();
        group.selectedToggleProperty().addListener((a,b,d) -> {
            if (d == null) {
                b.setSelected(true);
            }
        });

        ToggleButton home = new ToggleButton();
        home.setGraphic(getGraphic("/icons/home.png"));
        home.setMaxWidth(Double.MAX_VALUE);
        home.setToggleGroup(group);
        home.selectedProperty().addListener((obj, oldVal, newVal) -> {
            if (newVal) {
                productPane = new ProductPane(starter);
                setCenter(productPane);
                starter.getController().resetOffset();
                starter.getController().loadNextProducts();
            }
        });
        home.setSelected(true);

        ToggleButton cart = new ToggleButton();
        cart.setGraphic(getGraphic("/icons/cart.png"));
        cart.setMaxWidth(Double.MAX_VALUE);
        cart.setToggleGroup(group);
        cart.selectedProperty().addListener(buttonClickListener(new CartMenu()));

        ToggleButton orders = new ToggleButton();
        orders.setGraphic(getGraphic("/icons/arrow.png"));
        orders.setMaxWidth(Double.MAX_VALUE);
        orders.setToggleGroup(group);
        orders.selectedProperty().addListener(buttonClickListener(new OrderPane()));

        ToggleButton create = new ToggleButton();
        create.setGraphic(getGraphic("/icons/hammer.png"));
        create.setMaxWidth(Double.MAX_VALUE);
        create.setToggleGroup(group);
        create.selectedProperty().addListener((obj, oldVal, newVal) -> {
            if (newVal) {
                if (createPane == null){
                    createPane = new CreatePane(starter);
                }
                setCenter(createPane);
            }
        });

        ToggleButton crGoods = new ToggleButton();
        crGoods.setGraphic(getGraphic("/icons/box.png"));
        crGoods.setMaxWidth(Double.MAX_VALUE);
        crGoods.setToggleGroup(group);
        crGoods.selectedProperty().addListener((obj, oldVal, newVal) -> {
            if (newVal) {
                if (createdGoodsPane == null){
                    createdGoodsPane = new CreatedGoodsPane(starter);
                }
                setCenter(createdGoodsPane);
                starter.getController().loadCreatedProducts();
            }
        });

        ToggleButton profile = new ToggleButton();
        profile.setGraphic(getGraphic("/icons/account.png"));
        profile.setMaxWidth(Double.MAX_VALUE);
        profile.setToggleGroup(group);
        profile.selectedProperty().addListener((obj, oldVal, newVal) -> {
            if (newVal) {
                if (profilePane == null){
                    profilePane = new ProfilePane(starter);
                }
                setCenter(profilePane);
            }
        });

        buttonPane.add(home, 0, 0);
        buttonPane.add(cart, 1, 0);
        buttonPane.add(orders, 2, 0);
        buttonPane.add(create, 3, 0);
        buttonPane.add(crGoods, 4, 0);
        buttonPane.add(profile, 5, 0);

        setBottom(buttonPane);
    }

    private ImageView getGraphic(String path) {
        return new ImageView(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream(path))));
    }

    private ChangeListener<Boolean> buttonClickListener(Node node) {
        return (obj, oldVal, newVal) -> {
            if (newVal) {
                setCenter(node);
            }
        };
    }

    public CreatedGoodsPane getCreatedGoodsPane() {
        return createdGoodsPane;
    }

    public ProfilePane getProfilePane() {
        return profilePane;
    }

    public ProductPane getProductPane() {
        return productPane;
    }
}
