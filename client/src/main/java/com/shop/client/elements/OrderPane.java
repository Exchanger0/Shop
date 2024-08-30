package com.shop.client.elements;

import com.shop.client.Starter;
import com.shop.client.elements.views.OrderView;
import com.shop.common.model.Order;
import com.shop.common.model.Product;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OrderPane extends BorderPane {
    private final Starter starter;
    private final VBox orders = new VBox();
    public OrderPane(Starter starter) {
        this.starter = starter;
        Button makeOrder = new Button("Make order");
        BorderPane.setMargin(makeOrder, new Insets(10));
        makeOrder.setOnAction(e -> {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Make order");
            dialog.setHeaderText("Your cart:");
            dialog.getDialogPane().setMaxHeight(500);

            Map<Integer, Integer> productAmount = new HashMap<>();

            final BigDecimal[] totalPrice = {BigDecimal.ZERO};
            Label price = new Label("Price: " + totalPrice[0] + "$");

            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            GridPane gridPane = new GridPane();
            gridPane.setAlignment(Pos.CENTER);
            ColumnConstraints c1 = new ColumnConstraints();
            c1.setPercentWidth(40);
            ColumnConstraints c2 = new ColumnConstraints();
            c2.setPercentWidth(60);
            gridPane.getColumnConstraints().addAll(c1, c2);
            scrollPane.setContent(gridPane);

            int row = 0;
            for (Product pr : starter.getController().getCart()) {
                Spinner<Integer> amount = new Spinner<>(0, pr.getAmount(), 0);
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
                            if (i > pr.getAmount()) {
                                i = 0;
                            }
                        } catch (Exception ex) {
                            i = 0;
                        }
                        return i;
                    }
                });
                amount.valueProperty().addListener((obj, oldVal, newVal) -> {
                    if (newVal != 0) {
                        productAmount.put(pr.getId(), newVal);
                    }else {
                        productAmount.remove(pr.getId());
                    }
                    totalPrice[0] = totalPrice[0].subtract(pr.getPrice().multiply(BigDecimal.valueOf(oldVal)));
                    totalPrice[0] = totalPrice[0].add(pr.getPrice().multiply(BigDecimal.valueOf(newVal)));
                    price.setText("Price: " + totalPrice[0] + "$");
                });

                gridPane.add(new Label(pr.getName()), 0, row);
                gridPane.add(amount, 1, row);
                row++;
            }

            dialog.getDialogPane().setContent(new VBox(price, scrollPane));
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);
            Optional<ButtonType> res = dialog.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.APPLY) {
                starter.getController().makeOrder(productAmount, totalPrice[0]);
            }
        });
        setTop(makeOrder);
        setCenter(orders);
    }

    public void setOrders(List<Order> orders) {
        this.orders.getChildren().clear();
        for (Order o : orders) {
            this.orders.getChildren().add(new OrderView(starter, o));
        }
    }

    public void addOrder(Order order) {
        orders.getChildren().add(new OrderView(starter, order));
    }
}
