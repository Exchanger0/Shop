package com.shop.client.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class Product {
    private final int id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private int amount;
    private final List<byte[]> pictures;

    public Product(int id, String name, String description, BigDecimal price, int amount, List<byte[]> pictures) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.amount = amount;
        this.pictures = pictures;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public List<byte[]> getPictures() {
        return pictures;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id && Objects.equals(name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
