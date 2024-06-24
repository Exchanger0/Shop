package com.shop.server.model;

import jakarta.persistence.*;

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

    public User() {
    }

    public User(String username, String password, int balance) {
        this.username = username;
        this.password = password;
        this.balance = balance;
    }
}
