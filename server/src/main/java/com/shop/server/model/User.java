package com.shop.server.model;

import com.shop.common.UserType;
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
    @Enumerated(value = EnumType.STRING)
    @Column(name = "type")
    private UserType type;
    @Column(name = "balance")
    private int balance;

    public User() {
    }

    public User(String username, String password, UserType type, int balance) {
        this.username = username;
        this.password = password;
        this.type = type;
        this.balance = balance;
    }
}
