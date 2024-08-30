package com.shop.common.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class Order implements Serializable {
    private final int id;
    private final BigDecimal totalPrice;
    private final List<Product> products;

    public Order(int id, BigDecimal totalPrice, List<Product> products) {
        this.id = id;
        this.totalPrice = totalPrice;
        this.products = products;
    }

    public int getId() {
        return id;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public List<Product> getProducts() {
        return products;
    }
}
