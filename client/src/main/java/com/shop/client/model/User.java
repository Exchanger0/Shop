package com.shop.client.model;

import java.util.List;

public class User {
    private final String username;
    private int balance;
    private List<Product> createdProducts = null;

    public User(String username,  int balance) {
        this.username = username;
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getBalance() {
        return balance;
    }

    public void setCreatedProducts(List<Product> createdProducts) {
        this.createdProducts = createdProducts;
    }

    public List<Product> getCreatedProducts() {
        return createdProducts;
    }
}
