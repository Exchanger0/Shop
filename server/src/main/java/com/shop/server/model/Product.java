package com.shop.server.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "amount")
    private int amount;
    @OneToMany
    @JoinTable(name = "product_picture",
            joinColumns = @JoinColumn(name = "pr_id"),
            foreignKey = @ForeignKey(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "pi_id"),
            inverseForeignKey = @ForeignKey(name = "picture_id")
    )
    private List<Picture> pictures = new ArrayList<>();

    public Product() {
    }

    public Product(String name, String description, BigDecimal price, int amount, List<Picture> pictures) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.amount = amount;
        this.pictures = pictures;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

}
