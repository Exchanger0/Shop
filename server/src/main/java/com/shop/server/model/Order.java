package com.shop.server.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"order\"")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private int id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "total_price")
    private BigDecimal totalPrice;
    @OneToMany
    @JoinTable(name = "order_product",
            joinColumns = @JoinColumn(name = "order_id"),
            foreignKey = @ForeignKey(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"),
            inverseForeignKey = @ForeignKey(name = "product_id")
    )
    private List<Product> products = new ArrayList<>();

    public Order() {
    }

    public Order(User user, BigDecimal totalPrice, List<Product> products) {
        this.user = user;
        this.totalPrice = totalPrice;
        this.products = products;
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public List<Product> getProducts() {
        return products;
    }
}
