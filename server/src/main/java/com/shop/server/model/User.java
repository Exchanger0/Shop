package com.shop.server.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"user\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int id;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "balance")
    private int balance;
    @OneToMany
    @JoinTable(name = "created_product",
            joinColumns = @JoinColumn(name = "user_id"),
            foreignKey = @ForeignKey(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "pr_id"),
            inverseForeignKey = @ForeignKey(name = "product_id")
    )
    private List<Product> createdProducts = new ArrayList<>();
    @OneToMany
    @JoinTable(name = "cart",
            joinColumns = @JoinColumn(name = "user_id"),
            foreignKey = @ForeignKey(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "pr_id"),
            inverseForeignKey = @ForeignKey(name = "product_id")
    )
    private List<Product> cart = new ArrayList<>();
    @OneToMany(mappedBy = "user")
    private List<Order> orders = new ArrayList<>();

    public User() {
    }

    public User(String username, String password, int balance) {
        this.username = username;
        this.password = password;
        this.balance = balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getBalance() {
        return balance;
    }

    public List<Product> getCreatedProducts() {
        return createdProducts;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public List<Product> getCart() {
        return cart;
    }

    public List<Order> getOrders() {
        return orders;
    }
}
