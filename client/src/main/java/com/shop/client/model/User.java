package com.shop.client.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private final String username;
    private final String password;
    private final int balance;
    private List<Product> createdProducts = null;

    public User(String username, String password, int balance) {
        this.username = username;
        this.password = password;
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
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
