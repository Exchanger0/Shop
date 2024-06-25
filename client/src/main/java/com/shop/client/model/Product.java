package com.shop.client.model;

import java.math.BigDecimal;
import java.util.List;

public class Product {
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final List<byte[]> pictures;

    public Product(String name, String description, BigDecimal price, List<byte[]> pictures) {
        this.name = name;
        this.description = description;
        this.price = price;
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

    public List<byte[]> getPictures() {
        return pictures;
    }
}
