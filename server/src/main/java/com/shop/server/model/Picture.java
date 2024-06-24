package com.shop.server.model;

import jakarta.persistence.*;

@Entity
@Table(name = "picture")
public class Picture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "picture_id")
    private int id;
    @Column(name = "image")
    private byte[] image;

    public Picture() {
    }

    public Picture(byte[] image) {
        this.image = image;
    }

    public byte[] getImage() {
        return image;
    }
}
