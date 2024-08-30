package com.shop.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class User implements Serializable {
    private final String username;
    private int balance;
    private ArrayList<Product> createdProducts = null;
    private ArrayList<Product> cart = null;
    private ArrayList<Order> orders = null;

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

    public void setCreatedProducts(ArrayList<Product> createdProducts) {
        this.createdProducts = createdProducts;
    }

    public ArrayList<Product> getCreatedProducts() {
        return createdProducts;
    }

    public void setCart(ArrayList<Product> cart) {
        this.cart = cart;
    }

    public ArrayList<Product> getCart() {
        return cart;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }
}
