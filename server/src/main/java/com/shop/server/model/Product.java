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
    @OneToMany
    @JoinTable(name = "product_picture",
            joinColumns = @JoinColumn(name = "pr_id"),
            foreignKey = @ForeignKey(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "pi_id"),
            inverseForeignKey = @ForeignKey(name = "picture_id")
    )
    private List<Picture> pictures = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Product() {
    }

    public Product(String name, String description, BigDecimal price, List<Picture> pictures, User user) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.pictures = pictures;
        this.user = user;
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

    public List<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(List<Picture> pictures) {
        this.pictures = pictures;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
